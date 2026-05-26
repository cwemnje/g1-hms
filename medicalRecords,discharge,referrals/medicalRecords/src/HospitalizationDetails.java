import java.time.LocalDate;

public class HospitalizationDetails {
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private String duration;

    public HospitalizationDetails(LocalDate admissionDate, LocalDate dischargeDate, String duration) {
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.duration = duration;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public LocalDate getDischargeDate() {
        return dischargeDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            throw new IllegalArgumentException("Duration cannot be null or empty");
        }
        this.duration = duration;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        if (admissionDate == null) {
            throw new IllegalArgumentException("Admission date cannot be null");
        }
        this.admissionDate = admissionDate;
    }

    public void setDischargeDate(LocalDate dischargeDate) {
        if (dischargeDate == null) {
            throw new IllegalArgumentException("Discharge date cannot be null");
        }
        this.dischargeDate = dischargeDate;
    }

   public void displayHospitalizationDetails() {
        System.out.println("Admission Date: " + admissionDate);
        System.out.println("Discharge Date: " + dischargeDate);
        System.out.println("Duration: " + duration);
    }
}

