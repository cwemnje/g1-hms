import java.time.LocalDate;

public class TransitoryRecords extends MedRecords {
    private LocalDate appointmentDate;//rendez-vous date

    public TransitoryRecords(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}