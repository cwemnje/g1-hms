package com.hms.test;

import com.hms.database.DatabaseConnection;
import com.hms.models.*;
import com.hms.services.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class Main {

    // ── Counters ──────────────────────────────────────────
    private static int passed = 0;
    private static int failed = 0;

    // ── Shared state ──────────────────────────────────────
    private static String doctorId;
    private static String nurseId;
    private static String receptionistId;
    private static String pharmacistId;
    private static String labStaffId;
    private static String adminId;

    private static String patientId;
    private static String recordId;
    private static String appointmentId;
    private static String prescriptionId;
    private static String labOrderId;
    private static String invoiceId;
    private static int    medicationId;
    private static int    bedId;
    private static int    theatreId;
    private static int    dischargeId;
    private static int    allocationId;
    private static int    referralId;
    private static int    facilityId;

    // ═════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     HMS SYSTEM TEST — FULL COVERAGE      ║");
        System.out.println("╚══════════════════════════════════════════╝\n");

        clearDatabase();

        testAuth();
        testPatient();
        testSchedule();
        testAppointment();
        testMedicalRecord();
        testLab();
        testPrescription();
        testPharmacy();
        testAdmin();
        testBilling();
        testDischarge();
        testReferral();
        testReport();

        summary();
    }

    // ═════════════════════════════════════════════════════
    // CLEAR DATABASE
    // ═════════════════════════════════════════════════════
    private static void clearDatabase() {
        System.out.println("── Clearing all tables ──────────────────────");
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(
                "TRUNCATE TABLE " +
                "audit_trail, financial_report, " +
                "discharge_summary, patient_status, " +
                "receipt, payment, invoice_item, invoice, " +
                "reorder_request, dispensing_record, prescription_item, prescription, " +
                "test_result, sample, lab_order, " +
                "vitals, consultation_note, medical_record, " +
                "resource_allocation, " +
                "referral, external_facility, " +
                "nurse_schedule, doctor_schedule, " +
                "appointment, " +
                "medication, " +
                "nurse, lab_staff, pharmacist, doctor, " +
                "patient, users, " +
                "bed, theatre " +
                "RESTART IDENTITY CASCADE"
            );
            System.out.println("✔ All tables cleared.\n");
        } catch (SQLException e) {
            System.err.println("✘ Clear failed: " + e.getMessage() + "\n");
        }
    }

    // ═════════════════════════════════════════════════════
    // AUTH SERVICE
    // ═════════════════════════════════════════════════════
    private static void testAuth() {
        section("AUTH SERVICE");
        AuthService auth = new AuthService();

        User admin = buildUser("HMSA001", "admin", 1);
        check("Register admin", auth.registerUser(admin, "admin123"));
        adminId = "HMSA001";

        User doctor = buildUser("HMSD001", "doctor", 2);
        check("Register doctor", auth.registerUser(doctor, "doc123"));
        doctorId = "HMSD001";

        User nurse = buildUser("HMSN001", "nurse", 3);
        check("Register nurse", auth.registerUser(nurse, "nurse123"));
        nurseId = "HMSN001";

        User receptionist = buildUser("HMSR001", "receptionist", 4);
        check("Register receptionist", auth.registerUser(receptionist, "recept123"));
        receptionistId = "HMSR001";

        User pharmacist = buildUser("HMSP001", "pharmacist", 5);
        check("Register pharmacist", auth.registerUser(pharmacist, "pharma123"));
        pharmacistId = "HMSP001";

        User labTech = buildUser("HMSL001", "lab_technician", 6);
        check("Register lab technician", auth.registerUser(labTech, "lab123"));
        labStaffId = "HMSL001";

        // Login
        User loggedIn = auth.login("doctor@hms.com", "doc123");
        check("Login with correct credentials", loggedIn != null);

        User badLogin = auth.login("doctor@hms.com", "wrongpassword");
        check("Login with wrong password returns null", badLogin == null);

        // Deactivate and reactivate
        check("Deactivate user", auth.deactivateUser(nurseId));
        check("Reactivate user", auth.reactivateUser(nurseId));

        // Change password
        check("Change password", auth.changePassword(doctorId, "doc123", "newdoc123"));
        auth.changePassword(doctorId, "newdoc123", "doc123"); // restore

        // Reject duplicate email
        User duplicate = buildUser("HMSD002", "doctor", 2);
        check("Reject duplicate email", !auth.registerUser(duplicate, "pass"));
    }

    // ═════════════════════════════════════════════════════
    // PATIENT SERVICE
    // ═════════════════════════════════════════════════════
    private static void testPatient() {
        section("PATIENT SERVICE");
        PatientService ps = new PatientService();

        Patient p = new Patient();
        p.setFirstName("John");
        p.setLastName("Doe");
        p.setDateOfBirth(LocalDate.of(1990, 5, 15));
        p.setGender("Male");
        p.setPhone("677000001");
        p.setEmail("john.doe@email.com");
        p.setAddress("Buea, Cameroon");
        p.setInsuranceDetails("CNPS-2024");
        p.setOutstandingBillAmount(BigDecimal.ZERO);
        p.setPortalAccess(false);

        check("Register patient", ps.registerPatient(p));
        patientId = p.getPatientId();

        check("Search patient by ID", ps.searchPatientById(patientId) != null);
        check("Search patient by name", !ps.searchPatientByName("John").isEmpty());
        check("Get all patients", !ps.getAllPatients().isEmpty());

        p.setPhone("677000002");
        check("Update patient", ps.updatePatient(p));
        check("Patient exists", ps.patientExists(patientId));

        // Insert role-specific detail records
        insertDoctorDetail(doctorId);
        insertNurseDetail(nurseId);
        insertLabStaffDetail(labStaffId);
        insertPharmacistDetail(pharmacistId);
    }

    // ═════════════════════════════════════════════════════
    // SCHEDULE SERVICE
    // ═════════════════════════════════════════════════════
    private static void testSchedule() {
        section("SCHEDULE SERVICE");
        ScheduleService ss = new ScheduleService();

        // Doctor schedule — note: no created_by column in doctor_schedule
        DoctorSchedule ds = new DoctorSchedule();
        ds.setDoctorId(doctorId);
        ds.setShiftDate(LocalDate.now().plusDays(1));
        ds.setShiftStart(LocalTime.of(8, 0, 0));
        ds.setShiftEnd(LocalTime.of(16, 0, 0));
        ds.setDutyType("regular");
        check("Create doctor schedule", ss.createDoctorSchedule(ds));

        check("Get doctor schedules", !ss.getDoctorSchedules(doctorId).isEmpty());
        check("Get doctor schedules by date",
            !ss.getDoctorSchedulesByDate(LocalDate.now().plusDays(1)).isEmpty());

        // Conflict detection
        DoctorSchedule conflict = new DoctorSchedule();
        conflict.setDoctorId(doctorId);
        conflict.setShiftDate(LocalDate.now().plusDays(1));
        conflict.setShiftStart(LocalTime.of(9, 0, 0));
        conflict.setShiftEnd(LocalTime.of(14, 0, 0));
        conflict.setDutyType("regular");
        check("Reject conflicting doctor schedule", !ss.createDoctorSchedule(conflict));

        // Update — schedule_id 1 since we just inserted the first one
        ds.setScheduleId(1);
        ds.setDutyType("on_call");
        check("Update doctor schedule", ss.updateDoctorSchedule(ds));

        // Delete
        check("Delete doctor schedule", ss.deleteDoctorSchedule(1));

        // Nurse schedule
        NurseSchedule ns = new NurseSchedule();
        ns.setNurseId(nurseId);
        ns.setShiftDate(LocalDate.now().plusDays(1));
        ns.setShiftStart(LocalTime.of(7, 0, 0));
        ns.setShiftEnd(LocalTime.of(15, 0, 0));
        ns.setWardAssigned("Ward A");
        ns.setCreatedBy(adminId);
        check("Create nurse schedule", ss.createNurseSchedule(ns));

        check("Get nurse schedules", !ss.getNurseSchedules(nurseId).isEmpty());
        check("Get nurse schedules by date",
            !ss.getNurseSchedulesByDate(LocalDate.now().plusDays(1)).isEmpty());

        // Nurse conflict
        NurseSchedule nurseConflict = new NurseSchedule();
        nurseConflict.setNurseId(nurseId);
        nurseConflict.setShiftDate(LocalDate.now().plusDays(1));
        nurseConflict.setShiftStart(LocalTime.of(8, 0, 0));
        nurseConflict.setShiftEnd(LocalTime.of(12, 0, 0));
        nurseConflict.setWardAssigned("Ward B");
        nurseConflict.setCreatedBy(adminId);
        check("Reject conflicting nurse schedule", !ss.createNurseSchedule(nurseConflict));

        // Update nurse schedule
        ns.setScheduleId(1);
        ns.setWardAssigned("Ward B");
        check("Update nurse schedule", ss.updateNurseSchedule(ns));

        // Delete nurse schedule
        check("Delete nurse schedule", ss.deleteNurseSchedule(1));
    }

    // ═════════════════════════════════════════════════════
    // APPOINTMENT SERVICE
    // ═════════════════════════════════════════════════════
    private static void testAppointment() {
        section("APPOINTMENT SERVICE");
        AppointmentService as = new AppointmentService();

        LocalDateTime apptTime = LocalDateTime.now()
            .plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Appointment appt = new Appointment();
        appt.setPatientId(patientId);
        appt.setDoctorId(doctorId);
        appt.setReceptionistId(receptionistId);
        appt.setDateTime(apptTime);
        appt.setNotes("Routine checkup");
        check("Schedule appointment", as.scheduleAppointment(appt));
        appointmentId = appt.getAppointmentId();

        check("Get appointment by ID", as.getAppointmentById(appointmentId) != null);
        check("Get appointments by patient", !as.getAppointmentsByPatient(patientId).isEmpty());
        check("Get appointments by doctor", !as.getAppointmentsByDoctor(doctorId).isEmpty());
        check("Get today's appointments", as.getTodaysAppointments() != null);
        check("Doctor unavailable at booked time", !as.isDoctorAvailable(doctorId, apptTime));

        check("Check in patient", as.checkInPatient(appointmentId));

        LocalDateTime newTime = apptTime.plusDays(1);
        check("Reschedule appointment", as.rescheduleAppointment(appointmentId, newTime));
        check("Complete appointment", as.completeAppointment(appointmentId));

        // Second appointment — used as reference in medical record and discharge tests
        LocalDateTime apptTime2 = LocalDateTime.now()
            .plusDays(3).withHour(11).withMinute(0).withSecond(0).withNano(0);
        Appointment appt2 = new Appointment();
        appt2.setPatientId(patientId);
        appt2.setDoctorId(doctorId);
        appt2.setReceptionistId(receptionistId);
        appt2.setDateTime(apptTime2);
        appt2.setNotes("Follow-up visit");
        as.scheduleAppointment(appt2);
        appointmentId = appt2.getAppointmentId();

        check("Cancel appointment", as.cancelAppointment(appointmentId));
    }

    // ═════════════════════════════════════════════════════
    // MEDICAL RECORD SERVICE
    // ═════════════════════════════════════════════════════
    private static void testMedicalRecord() {
        section("MEDICAL RECORD SERVICE");
        MedicalRecordService mrs = new MedicalRecordService();

        MedicalRecord record = new MedicalRecord();
        record.setPatientId(patientId);
        record.setDiagnosis("Initial diagnosis");
        record.setTreatmentHistory("None");
        check("Create medical record", mrs.createMedicalRecord(record));
        recordId = record.getRecordId();

        check("Reject duplicate medical record", !mrs.createMedicalRecord(record));
        check("Get record by patient ID", mrs.getRecordByPatientId(patientId) != null);
        check("Get record by record ID", mrs.getRecordById(recordId) != null);

        record.setDiagnosis("Viral fever");
        record.setTreatmentHistory("Paracetamol 500mg");
        check("Update medical record", mrs.updateMedicalRecord(record));

        // Consultation note
        ConsultationNote note = new ConsultationNote();
        note.setRecordId(recordId);
        note.setDoctorId(doctorId);
        note.setAppointmentId(appointmentId);
        note.setNote("Patient presents with mild fever and fatigue.");
        check("Add consultation note", mrs.addConsultationNote(note));
        check("Get consultation notes", !mrs.getConsultationNotes(recordId).isEmpty());

        // Vitals — temperature kept small to fit NUMERIC(4,1)
        Vitals vitals = new Vitals();
        vitals.setRecordId(recordId);
        vitals.setNurseId(nurseId);
        vitals.setBloodPressure("120/80");
        vitals.setTemperature(new BigDecimal("37.2"));
        vitals.setPulseRate(72);
        vitals.setRespiratoryRate(16);
        vitals.setWeight(new BigDecimal("70.50"));
        vitals.setHeight(new BigDecimal("175.00"));
        check("Record vitals", mrs.recordVitals(vitals));
        check("Get vitals", !mrs.getVitals(recordId).isEmpty());

        check("Record exists for patient", mrs.recordExistsForPatient(patientId));
    }

    // ═════════════════════════════════════════════════════
    // LAB SERVICE
    // ═════════════════════════════════════════════════════
    private static void testLab() {
        section("LAB SERVICE");
        LabService ls = new LabService();

        LabOrder order = new LabOrder();
        order.setPatientId(patientId);
        order.setDoctorId(doctorId);
        order.setTestType("Full Blood Count");
        order.setUrgent(false);
        check("Order lab test", ls.orderLabTest(order));
        labOrderId = order.getOrderId();

        check("Get lab order by ID", ls.getLabOrderById(labOrderId) != null);
        check("Get lab orders by patient", !ls.getLabOrdersByPatient(patientId).isEmpty());
        check("Get pending lab orders", !ls.getPendingLabOrders().isEmpty());

        // Collect sample
        Sample sample = new Sample();
        sample.setOrderId(labOrderId);
        sample.setLabStaffId(labStaffId);
        check("Collect sample", ls.collectSample(sample));
        check("Update sample status", ls.updateSampleStatus(1, "processing"));

        // Enter results
        TestResult result = new TestResult();
        result.setOrderId(labOrderId);
        result.setLabStaffId(labStaffId);
        result.setRecordId(recordId);
        result.setResultValue("Haemoglobin: 13.5 g/dL — Normal");
        result.setNotes("No abnormalities detected.");
        check("Enter test results", ls.enterTestResults(result));
        check("Get test results by order", !ls.getTestResults(labOrderId).isEmpty());
        check("Get test results by record", !ls.getTestResultsByRecord(recordId).isEmpty());

        // Urgent order
        LabOrder urgent = new LabOrder();
        urgent.setPatientId(patientId);
        urgent.setDoctorId(doctorId);
        urgent.setTestType("Malaria RDT");
        urgent.setUrgent(true);
        check("Order urgent lab test", ls.orderLabTest(urgent));
    }

    // ═════════════════════════════════════════════════════
    // PRESCRIPTION SERVICE
    // ═════════════════════════════════════════════════════
    private static void testPrescription() {
        section("PRESCRIPTION SERVICE");
        PrescriptionService ps = new PrescriptionService();

        // Add medication to inventory
        Medication med = new Medication();
        med.setMedicationName("Amoxicillin 500mg");
        med.setStockLevel(100);
        med.setReorderThreshold(10);
        med.setUnitPrice(new BigDecimal("500.00"));
        med.setExpiryDate(LocalDate.of(2027, 12, 31));
        check("Add medication to inventory", ps.addMedication(med));

        List<Medication> meds = ps.getAllMedications();
        check("Get all medications", !meds.isEmpty());
        medicationId = meds.get(0).getMedicationId();
        check("Get medication by ID", ps.getMedicationById(medicationId) != null);

        // Create prescription
        Prescription prescription = new Prescription();
        prescription.setPatientId(patientId);
        prescription.setDoctorId(doctorId);
        prescription.setRecordId(recordId);
        prescription.setAllergyChecked(true);
        check("Create prescription", ps.createPrescription(prescription));
        prescriptionId = prescription.getPrescriptionId();

        // Add item
        PrescriptionItem item = new PrescriptionItem();
        item.setPrescriptionId(prescriptionId);
        item.setMedicationId(medicationId);
        item.setDosage("500mg twice daily");
        item.setInstructions("Take after meals");
        item.setQuantity(10);
        check("Add prescription item", ps.addPrescriptionItem(item));

        check("Get prescription by ID", ps.getPrescriptionById(prescriptionId) != null);
        check("Get prescriptions by patient", !ps.getPrescriptionsByPatient(patientId).isEmpty());
        check("Get pending prescriptions", !ps.getPendingPrescriptions().isEmpty());
        check("Get prescription items", !ps.getPrescriptionItems(prescriptionId).isEmpty());

        // Dispense
        DispensingRecord dispRecord = new DispensingRecord();
        dispRecord.setPrescriptionId(prescriptionId);
        dispRecord.setPharmacistId(pharmacistId);
        dispRecord.setNotes("Dispensed successfully");
        check("Dispense prescription", ps.dispensePrescription(dispRecord));

        // Reject double dispense
        check("Reject dispensing already dispensed prescription",
            !ps.dispensePrescription(dispRecord));
    }

    // ═════════════════════════════════════════════════════
    // PHARMACY SERVICE
    // ═════════════════════════════════════════════════════
    private static void testPharmacy() {
        section("PHARMACY SERVICE");
        PharmacyService pharma = new PharmacyService();

        check("Get all medications", !pharma.getAllMedications().isEmpty());
        check("Get medication by ID", pharma.getMedicationById(medicationId) != null);
        check("Update stock level", pharma.updateStockLevel(medicationId, 200));

        Medication med = pharma.getMedicationById(medicationId);
        med.setReorderThreshold(20);
        med.setUnitPrice(new BigDecimal("450.00"));
        check("Update medication details", pharma.updateMedication(med));

        // Trigger low stock
        pharma.updateStockLevel(medicationId, 5);
        check("Get low stock medications", !pharma.getLowStockMedications().isEmpty());

        // Reorder request
        ReorderRequest req = new ReorderRequest();
        req.setMedicationId(medicationId);
        req.setPharmacistId(pharmacistId);
        req.setQuantityRequested(100);
        req.setJustification("Stock critically low");
        check("Raise reorder request", pharma.raiseReorderRequest(req));

        List<ReorderRequest> pending = pharma.getPendingReorderRequests();
        check("Get pending reorder requests", !pending.isEmpty());
        int requestId = pending.get(0).getRequestId();
        check("Approve reorder request", pharma.approveReorderRequest(requestId, adminId, 100));

        // Raise and reject another
        ReorderRequest req2 = new ReorderRequest();
        req2.setMedicationId(medicationId);
        req2.setPharmacistId(pharmacistId);
        req2.setQuantityRequested(50);
        req2.setJustification("Additional buffer stock");
        pharma.raiseReorderRequest(req2);

        List<ReorderRequest> pending2 = pharma.getPendingReorderRequests();
        check("Reject reorder request",
            pharma.rejectReorderRequest(pending2.get(0).getRequestId(), adminId));
    }

    // ═════════════════════════════════════════════════════
    // ADMIN SERVICE
    // ═════════════════════════════════════════════════════
    private static void testAdmin() {
        section("ADMIN SERVICE");
        AdminService admin = new AdminService();

        check("Get all users", !admin.getAllUsers().isEmpty());
        check("Get user by ID", admin.getUserById(doctorId) != null);
        check("Get users by role", !admin.getUsersByRole(2).isEmpty());
        check("Update access permissions", admin.updateAccessPermissions(nurseId, false));
        admin.updateAccessPermissions(nurseId, true); // restore

        // Beds
        Bed bed = new Bed();
        bed.setWardName("Ward A");
        bed.setBedNumber("A01");
        check("Add bed", admin.addBed(bed));

        Bed bed2 = new Bed();
        bed2.setWardName("Ward B");
        bed2.setBedNumber("B01");
        admin.addBed(bed2);

        List<Bed> available = admin.getAvailableBeds();
        check("Get available beds", !available.isEmpty());
        bedId = available.get(0).getBedId();
        check("Get all beds", !admin.getAllBeds().isEmpty());
        check("Update bed status to occupied", admin.updateBedStatus(bedId, "occupied"));
        check("Update bed status back to available", admin.updateBedStatus(bedId, "available"));

        // Theatres
        Theatre theatre = new Theatre();
        theatre.setTheatreName("Theatre 1");
        check("Add theatre", admin.addTheatre(theatre));

        List<Theatre> theatres = admin.getAvailableTheatres();
        check("Get available theatres", !theatres.isEmpty());
        theatreId = theatres.get(0).getTheatreId();
        check("Get all theatres", !admin.getAllTheatres().isEmpty());

        // Resource allocation
        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setPatientId(patientId);
        allocation.setDoctorId(doctorId);
        allocation.setApprovedBy(adminId);
        allocation.setBedId(bedId);
        allocation.setAllocationType("bed");
        allocation.setEmergency(false);
        check("Allocate bed to patient", admin.allocateResource(allocation));

        List<ResourceAllocation> active = admin.getActiveAllocations();
        check("Get active allocations", !active.isEmpty());
        allocationId = active.get(0).getAllocationId();
        check("Release resource", admin.releaseResource(allocationId));
    }

    // ═════════════════════════════════════════════════════
    // BILLING SERVICE
    // ═════════════════════════════════════════════════════
    private static void testBilling() {
        section("BILLING SERVICE");
        BillingService bs = new BillingService();

        Invoice invoice = new Invoice();
        invoice.setPatientId(patientId);
        invoice.setReceptionistId(receptionistId);
        invoice.setTotalAmount(BigDecimal.ZERO);
        invoice.setInsuranceDeduction(BigDecimal.ZERO);
        invoice.setAmountDue(BigDecimal.ZERO);
        check("Generate invoice", bs.generateInvoice(invoice));
        invoiceId = invoice.getInvoiceId();

        // Add itemized charges
        InvoiceItem item1 = new InvoiceItem();
        item1.setInvoiceId(invoiceId);
        item1.setDescription("Consultation fee");
        item1.setChargeType("consultation");
        item1.setAmount(new BigDecimal("5000.00"));
        check("Add consultation charge", bs.addInvoiceItem(item1));

        InvoiceItem item2 = new InvoiceItem();
        item2.setInvoiceId(invoiceId);
        item2.setDescription("Full Blood Count test");
        item2.setChargeType("lab_test");
        item2.setAmount(new BigDecimal("3000.00"));
        check("Add lab test charge", bs.addInvoiceItem(item2));

        InvoiceItem item3 = new InvoiceItem();
        item3.setInvoiceId(invoiceId);
        item3.setDescription("Amoxicillin 500mg x10");
        item3.setChargeType("medication");
        item3.setAmount(new BigDecimal("5000.00"));
        check("Add medication charge", bs.addInvoiceItem(item3));

        check("Get invoice by ID", bs.getInvoiceById(invoiceId) != null);
        check("Get invoices by patient", !bs.getInvoicesByPatient(patientId).isEmpty());
        check("Get invoice items", !bs.getInvoiceItems(invoiceId).isEmpty());
        check("Get unpaid invoices", !bs.getUnpaidInvoices().isEmpty());

        // Insurance deduction
        check("Apply insurance deduction",
            bs.applyInsuranceDeduction(invoiceId, new BigDecimal("2000.00")));

        // Partial payment — total is 13000, deduction 2000, so amount_due = 11000
        Payment payment1 = new Payment();
        payment1.setInvoiceId(invoiceId);
        payment1.setAmountPaid(new BigDecimal("5000.00"));
        payment1.setPaymentMethod("cash");
        payment1.setProcessedBy(receptionistId);
        check("Process partial payment", bs.processPayment(payment1));

        Invoice afterPartial = bs.getInvoiceById(invoiceId);
        check("Invoice status is partially_paid",
            afterPartial != null && afterPartial.getStatus().equals("partially_paid"));

        // Final payment — covers remaining 6000
        Payment payment2 = new Payment();
        payment2.setInvoiceId(invoiceId);
        payment2.setAmountPaid(new BigDecimal("6000.00"));
        payment2.setPaymentMethod("mobile_money");
        payment2.setProcessedBy(receptionistId);
        check("Process final payment", bs.processPayment(payment2));

        Invoice afterFinal = bs.getInvoiceById(invoiceId);
        check("Invoice status is settled",
            afterFinal != null && afterFinal.getStatus().equals("settled"));

        // Reject payment on settled invoice
        Payment payment3 = new Payment();
        payment3.setInvoiceId(invoiceId);
        payment3.setAmountPaid(new BigDecimal("1000.00"));
        payment3.setPaymentMethod("cash");
        payment3.setProcessedBy(receptionistId);
        check("Reject payment on settled invoice", !bs.processPayment(payment3));

        check("Get payments by invoice", !bs.getPaymentsByInvoice(invoiceId).isEmpty());
        check("Update patient outstanding bill",
            bs.updatePatientOutstandingBill(patientId, BigDecimal.ZERO));
    }

    // ═════════════════════════════════════════════════════
    // DISCHARGE SERVICE
    // ═════════════════════════════════════════════════════
    private static void testDischarge() {
        section("DISCHARGE SERVICE");
        DischargeService ds = new DischargeService();

        check("Set patient status to admitted",
            ds.updatePatientStatus(patientId, "admitted"));
        check("Get patient status", ds.getPatientStatus(patientId) != null);
        check("Update patient status to ready_for_discharge",
            ds.updatePatientStatus(patientId, "ready_for_discharge"));

        // Reject discharge with nonexistent invoice
        DischargeSummary badSummary = new DischargeSummary();
        badSummary.setPatientId(patientId);
        badSummary.setDoctorId(doctorId);
        badSummary.setRecordId(recordId);
        badSummary.setInvoiceId("INVS999");
        badSummary.setDiagnosisSummary("Viral fever");
        badSummary.setTreatmentSummary("Antibiotics prescribed");
        badSummary.setMedicationsSummary("Amoxicillin 500mg");
        badSummary.setFollowupInstructions("Return in 7 days if symptoms persist.");
        badSummary.setPatientCondition("stable");
        check("Reject discharge with unpaid invoice", !ds.createDischargeSummary(badSummary));

        // Proper discharge — invoice is settled
        DischargeSummary summary = new DischargeSummary();
        summary.setPatientId(patientId);
        summary.setDoctorId(doctorId);
        summary.setRecordId(recordId);
        summary.setInvoiceId(invoiceId);
        summary.setDiagnosisSummary("Viral fever — resolved");
        summary.setTreatmentSummary("Antibiotics and rest");
        summary.setMedicationsSummary("Amoxicillin 500mg x10");
        summary.setFollowupInstructions("Return in 7 days if symptoms persist.");
        summary.setPatientCondition("stable");
        check("Create discharge summary", ds.createDischargeSummary(summary));

        DischargeSummary retrieved = ds.getDischargeSummaryByPatient(patientId);
        check("Get discharge summary by patient", retrieved != null);
        dischargeId = retrieved != null ? retrieved.getDischargeId() : 0;

        check("Get discharge summary by ID", ds.getDischargeSummaryById(dischargeId) != null);

        summary.setDischargeId(dischargeId);
        summary.setPatientCondition("recovering");
        check("Update discharge summary", ds.updateDischargeSummary(summary));
        check("Finalize discharge summary", ds.finalizeDischargeSummary(dischargeId));
        check("Set patient status to discharged",
            ds.updatePatientStatus(patientId, "discharged"));
    }

    // ═════════════════════════════════════════════════════
    // REFERRAL SERVICE
    // ═════════════════════════════════════════════════════
    private static void testReferral() {
        section("REFERRAL SERVICE");
        ReferralService rs = new ReferralService();

        ExternalFacility facility = new ExternalFacility();
        facility.setFacilityName("Buea Regional Hospital");
        facility.setLocation("Buea, SW Region");
        facility.setContact("233000001");
        facility.setSpecialization("Cardiology");
        facility.setAddedBy(adminId);
        check("Add external facility", rs.addExternalFacility(facility));

        List<ExternalFacility> facilities = rs.getAllFacilities();
        check("Get all facilities", !facilities.isEmpty());
        facilityId = facilities.get(0).getFacilityId();
        check("Get facility by ID", rs.getFacilityById(facilityId) != null);

        facility.setFacilityId(facilityId);
        facility.setContact("233000002");
        check("Update external facility", rs.updateExternalFacility(facility));

        Referral referral = new Referral();
        referral.setPatientId(patientId);
        referral.setDoctorId(doctorId);
        referral.setFacilityId(facilityId);
        referral.setReferralDetails("Patient needs cardiology review.");
        referral.setJustification("Suspected arrhythmia.");
        check("Create referral", rs.createReferral(referral));

        List<Referral> byPatient = rs.getReferralsByPatient(patientId);
        check("Get referrals by patient", !byPatient.isEmpty());
        referralId = byPatient.get(0).getReferralId();

        check("Get referral by ID", rs.getReferralById(referralId) != null);
        check("Get referrals by doctor", !rs.getReferralsByDoctor(doctorId).isEmpty());
        check("Get pending referrals", !rs.getPendingReferrals().isEmpty());
        check("Update referral status to completed",
            rs.updateReferralStatus(referralId, "completed"));
    }

    // ═════════════════════════════════════════════════════
    // REPORT SERVICE
    // ═════════════════════════════════════════════════════
    private static void testReport() {
        section("REPORT SERVICE");
        ReportService report = new ReportService();

        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to   = LocalDate.now();

        BigDecimal revenue = report.getTotalRevenue(from, to);
        check("Get total revenue (> 0)",
            revenue != null && revenue.compareTo(BigDecimal.ZERO) > 0);

        BigDecimal outstanding = report.getTotalOutstanding();
        check("Get total outstanding", outstanding != null);

        check("Get payments by method", !report.getPaymentsByMethod(from, to).isEmpty());
        check("Get invoice summary by status", !report.getInvoiceSummaryByStatus().isEmpty());
        check("Get total patients registered", report.getTotalPatientsRegistered(from, to) > 0);
        check("Get total appointments", report.getTotalAppointments(from, to) >= 0);
        check("Get total lab orders", report.getTotalLabOrders(from, to) > 0);

        // report_type CHECK is now broadened so monthly_revenue is valid
        FinancialReport fr = new FinancialReport();
        fr.setGeneratedBy(adminId);
        fr.setReportType("monthly_revenue");
        fr.setDateFrom(from);
        fr.setDateTo(to);
        fr.setDepartment("All");
        fr.setPaymentStatusFilter("settled");
        check("Save financial report", report.saveReport(fr));
        check("Get all reports", !report.getAllReports().isEmpty());
    }

    // ═════════════════════════════════════════════════════
    // HELPERS
    // ═════════════════════════════════════════════════════

    private static User buildUser(String userId, String role, int roleId) {
        User u = new User();
        u.setUserId(userId);
        u.setFirstName(capitalize(role));
        u.setLastName("Test");
        u.setEmail(role + "@hms.com");
        u.setPhone("6770000" + roleId);
        u.setRoleId(roleId);
        u.setShiftSchedule("Morning");
        u.setAccessPermissions(true);
        u.setActive(true);
        return u;
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static void insertDoctorDetail(String id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().execute(
                "INSERT INTO doctor (doctor_id, specialization, personal_availability) " +
                "VALUES ('" + id + "', 'General Practice', true) ON CONFLICT DO NOTHING"
            );
        } catch (SQLException e) {
            System.err.println("insertDoctorDetail error: " + e.getMessage());
        }
    }

    private static void insertNurseDetail(String id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().execute(
                "INSERT INTO nurse (nurse_id, ward_assignment) " +
                "VALUES ('" + id + "', 'Ward A') ON CONFLICT DO NOTHING"
            );
        } catch (SQLException e) {
            System.err.println("insertNurseDetail error: " + e.getMessage());
        }
    }

    private static void insertLabStaffDetail(String id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().execute(
                "INSERT INTO lab_staff (lab_staff_id, lab_access_level) " +
                "VALUES ('" + id + "', 'full') ON CONFLICT DO NOTHING"
            );
        } catch (SQLException e) {
            System.err.println("insertLabStaffDetail error: " + e.getMessage());
        }
    }

    private static void insertPharmacistDetail(String id) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.createStatement().execute(
                "INSERT INTO pharmacist (pharmacist_id, inventory_access_level) " +
                "VALUES ('" + id + "', 'full') ON CONFLICT DO NOTHING"
            );
        } catch (SQLException e) {
            System.err.println("insertPharmacistDetail error: " + e.getMessage());
        }
    }

    private static void check(String testName, boolean result) {
        if (result) {
            System.out.println("  ✔ PASS — " + testName);
            passed++;
        } else {
            System.out.println("  ✘ FAIL — " + testName);
            failed++;
        }
    }

    private static void section(String title) {
        System.out.println("\n── " + title + " " + "─".repeat(Math.max(0, 44 - title.length())));
    }

    private static void summary() {
        int total = passed + failed;
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║              TEST SUMMARY                ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.printf( "║  Total   : %-31d║%n", total);
        System.out.printf( "║  Passed  : %-31d║%n", passed);
        System.out.printf( "║  Failed  : %-31d║%n", failed);
        System.out.printf( "║  Score   : %-30s ║%n",
            String.format("%.1f%%", total > 0 ? (passed * 100.0 / total) : 0));
        System.out.println("╚══════════════════════════════════════════╝");
        DatabaseConnection.closeConnection();
    }
}