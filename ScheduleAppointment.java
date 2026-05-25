import java.util.ArrayList;
import java.util.List;

/*
 * =============================================================================
 * HOSPITAL MANAGEMENT SYSTEM — SCHEDULE APPOINTMENT USE CASE
 * =============================================================================
 * This file implements the Schedule Appointment use case.
 * It contains 5 classes:
 *   1. Person        — Base class (inherited by Doctor, Patient, Receptionist)
 *   2. Doctor        — Extends Person, represents the doctor being booked
 *   3. Patient       — Extends Person, represents the patient being scheduled
 *   4. Appointment   — Core entity with confirm, cancel, reschedule methods
 *   5. Receptionist  — Extends Person, drives the scheduling use case
 *
 * Entry point: ScheduleAppointment.main()
 * =============================================================================
 */


// =============================================================================
// CLASS 1: Person — BASE CLASS
// =============================================================================
// Purpose: Holds attributes common to Doctor, Patient and Receptionist.
// Inheritance: Doctor, Patient and Receptionist all extend this class,
//              so they automatically inherit name and contactInfo.
// =============================================================================
class Person {

    // ---------- Attributes ----------
    private String name;        // Full name of the person
    private int    contactInfo; // Phone number

    // ---------- Constructor ----------
    // Called via super() in subclasses (Doctor, Patient, Receptionist)
    public Person(String name, int contactInfo) {
        this.name        = name;
        this.contactInfo = contactInfo;
    }

    // ---------- Getters ----------
    // Getters are defined here so all subclasses can access name and contactInfo
    public String getName()        { return name; }
    public int    getContactInfo() { return contactInfo; }

} // end class Person


// =============================================================================
// CLASS 2: Doctor — EXTENDS Person
// =============================================================================
// Purpose: Represents a doctor who can be assigned to an appointment.
// Inheritance: Extends Person, so it inherits getName() and getContactInfo().
// =============================================================================
class Doctor extends Person {

    // ---------- Attributes ----------
    private String  staffID;              // Unique doctor ID
    private String  specialty;            // e.g. Cardiology, Neurology
    private boolean personalAvailability; // true = available, false = booked

    // ---------- Constructor ----------
    // super(name, contactInfo) passes shared attributes up to Person
    public Doctor(String staffID, String name, int contactInfo,
                  String specialty, boolean personalAvailability) {
        super(name, contactInfo); // Call Person constructor
        this.staffID              = staffID;
        this.specialty            = specialty;
        this.personalAvailability = personalAvailability;
    }

    // ---------- Getters ----------
    public String  getStaffID()   { return staffID; }
    public String  getSpecialty() { return specialty; }
    public boolean isAvailable()  { return personalAvailability; } // Used before scheduling

    // ---------- Setter ----------
    // This setter is necessary — availability changes when a doctor is booked or freed
    public void setPersonalAvailability(boolean avail) {
        this.personalAvailability = avail;
    }

} // end class Doctor


// =============================================================================
// CLASS 3: Patient — EXTENDS Person
// =============================================================================
// Purpose: Represents the patient for whom the appointment is being scheduled.
// Inheritance: Extends Person, so it inherits getName() and getContactInfo().
// =============================================================================
class Patient extends Person {

    // ---------- Attributes ----------
    private String patientID; // Unique patient ID
    private String dob;       // Date of birth e.g. "1990-05-12"

    // ---------- Constructor ----------
    // super(name, contactInfo) passes shared attributes up to Person
    public Patient(String patientID, String name, int contactInfo, String dob) {
        super(name, contactInfo); // Call Person constructor
        this.patientID = patientID;
        this.dob       = dob;
    }

    // ---------- Getters ----------
    public String getPatientID() { return patientID; }
    public String getDob()       { return dob; }

} // end class Patient


// =============================================================================
// CLASS 4: Appointment — CORE ENTITY
// =============================================================================
// Purpose: Represents a scheduled appointment between a patient and a doctor.
// Methods: confirm(), cancel(), reschedule() — directly from the class diagram.
// Note: Does not extend Person — it is not a person, it is an event/entity.
// =============================================================================
class Appointment {

    // ---------- Status Options (Enum) ----------
    // Tracks the current state of the appointment throughout its lifecycle
    public enum Status {
        SCHEDULED,   // Newly created, not yet confirmed
        CONFIRMED,   // Confirmed by receptionist
        CANCELLED,   // Cancelled before it took place
        RESCHEDULED  // Moved to a new date/time
    }

    // ---------- Attributes ----------
    private int     appointmentID; // Unique ID auto-generated by Receptionist
    private String  dateTime;      // Appointment date and time e.g. "2026-05-25 09:00"
    private String  status;        // Current status (from enum above)
    private Doctor  doctor;        // The doctor assigned to this appointment
    private Patient patient;       // The patient this appointment belongs to

    // ---------- Constructor ----------
    // Default status is set to SCHEDULED when a new appointment is created
    public Appointment(int appointmentID, String dateTime, Doctor doctor, Patient patient) {
        this.appointmentID = appointmentID;
        this.dateTime      = dateTime;
        this.doctor        = doctor;
        this.patient       = patient;
        this.status        = Status.SCHEDULED.name(); // Initial status
    }

    // ---------- Methods from Class Diagram ----------

    // confirm() — called right after appointment is created by Receptionist
    public void confirm() {
        this.status = Status.CONFIRMED.name();
        System.out.println("Appointment " + appointmentID + " confirmed.");
    }

    // cancel() — called when Receptionist cancels an appointment
    public void cancel() {
        this.status = Status.CANCELLED.name();
        System.out.println("Appointment " + appointmentID + " cancelled.");
    }

    // reschedule() — updates the date/time and changes status to RESCHEDULED
    public void reschedule(String newDateTime) {
        this.dateTime = newDateTime;
        this.status   = Status.RESCHEDULED.name();
        System.out.println("Appointment " + appointmentID + " rescheduled to " + newDateTime);
    }

    // ---------- Getters ----------
    // Used by Receptionist and main() to read appointment details
    public int     getAppointmentID() { return appointmentID; }
    public String  getDateTime()      { return dateTime; }
    public String  getStatus()        { return status; }
    public Doctor  getDoctor()        { return doctor; }   // Needed to free doctor after cancel
    public Patient getPatient()       { return patient; }

    // ---------- toString ----------
    // Formats the appointment details for printing
    @Override
    public String toString() {
        return String.format(
            "Appointment[ID=%d | Patient=%s | Doctor=%s | DateTime=%s | Status=%s]",
            appointmentID, patient.getName(), doctor.getName(), dateTime, status);
    }

} // end class Appointment


// =============================================================================
// CLASS 5: Receptionist — EXTENDS Person
// =============================================================================
// Purpose: The actor who performs the Schedule Appointment use case.
//          Creates, cancels and reschedules appointments.
// Inheritance: Extends Person, so it inherits getName() and getContactInfo().
// =============================================================================
class Receptionist extends Person {

    // ---------- Attributes ----------
    private String            staffID;      // Unique receptionist ID
    private List<Appointment> appointments; // List of all appointments managed
    private int               nextID;       // Auto-increments appointment IDs

    // ---------- Constructor ----------
    public Receptionist(String staffID, String name, int contactInfo) {
        super(name, contactInfo); // Call Person constructor
        this.staffID      = staffID;
        this.appointments = new ArrayList<>(); // Initialise empty appointments list
        this.nextID       = 1;                 // Start appointment IDs from 1
    }

    // ---------- Getter ----------
    public String getStaffID() { return staffID; }

    // ---------- Use Case Methods (from Class Diagram) ----------

    /*
     * scheduleAppointment()
     * ---------------------
     * Main use case method. Steps:
     *   1. Check if the doctor is available
     *   2. Create a new Appointment object
     *   3. Confirm the appointment
     *   4. Add it to the appointments list
     *   5. Mark the doctor as unavailable (occupied)
     */
    public Appointment scheduleAppointment(Patient patient, Doctor doctor, String dateTime) {

        // Step 1: Check doctor availability before proceeding
        if (!doctor.isAvailable()) {
            System.out.println("Cannot schedule — Dr. " + doctor.getName() + " is not available.");
            return null; // Exit early if doctor is busy
        }

        // Step 2: Create new appointment with auto-incremented ID
        Appointment appointment = new Appointment(nextID++, dateTime, doctor, patient);

        // Step 3: Confirm the appointment
        appointment.confirm();

        // Step 4: Save appointment to the list
        appointments.add(appointment);

        // Step 5: Mark doctor as occupied so no double-booking occurs
        doctor.setPersonalAvailability(false);

        System.out.println("Scheduled: " + appointment);
        return appointment;
    }

    /*
     * cancelAppointment()
     * -------------------
     * Cancels an existing appointment and frees the doctor.
     */
    public void cancelAppointment(Appointment appointment) {
        appointment.cancel();                              // Update appointment status
        appointment.getDoctor().setPersonalAvailability(true); // Free the doctor
    }

    /*
     * rescheduleAppointment()
     * -----------------------
     * Moves an existing appointment to a new date and time.
     */
    public void rescheduleAppointment(Appointment appointment, String newDateTime) {
        appointment.reschedule(newDateTime); // Delegate to Appointment's reschedule method
    }

    /*
     * printAllAppointments()
     * ----------------------
     * Displays all appointments managed by this receptionist.
     */
    public void printAllAppointments() {
        System.out.println("\n--- All Appointments ---");
        appointments.forEach(System.out::println); // Print each appointment using toString()
    }

} // end class Receptionist


// =============================================================================
// MAIN — ENTRY POINT
// =============================================================================
// Purpose: Demonstrates the full Schedule Appointment use case.
//          Creates actors, schedules, reschedules and cancels appointments.
// =============================================================================
public class ScheduleAppointment {

    public static void main(String[] args) {

        // ---------- Create Actors ----------
        Receptionist receptionist = new Receptionist("R001", "Ama Serwaa",    244000111);
        Doctor  doctor1  = new Doctor("D001", "Dr. Amara Mensah", 244111222, "Cardiology", true);
        Doctor  doctor2  = new Doctor("D002", "Dr. Kwame Asante", 244333444, "Neurology",  true);
        Patient patient1 = new Patient("P001", "Emmanuel Tetteh", 244555666, "1990-05-12");
        Patient patient2 = new Patient("P002", "Adjoa Amponsah",  244777888, "1985-08-23");

        System.out.println("=== Schedule Appointment Use Case ===\n");

        // ---------- Schedule Appointments ----------
        Appointment a1 = receptionist.scheduleAppointment(patient1, doctor1, "2026-05-25 09:00");
        Appointment a2 = receptionist.scheduleAppointment(patient2, doctor2, "2026-05-25 10:00");

        // ---------- Attempt Double Booking (should fail) ----------
        // doctor1 is already booked, so this should print an error
        receptionist.scheduleAppointment(patient2, doctor1, "2026-05-25 11:00");

        // ---------- Reschedule Appointment ----------
        System.out.println("\n-- Rescheduling a1 --");
        receptionist.rescheduleAppointment(a1, "2026-05-26 14:00");

        // ---------- Cancel Appointment ----------
        System.out.println("\n-- Cancelling a2 --");
        receptionist.cancelAppointment(a2);

        // ---------- Display All Appointments ----------
        receptionist.printAllAppointments();

    } // end main

} // end class ScheduleAppointment
