import java.time.LocalDate;

public class ClinicalVisitHistory {
    private LocalDate LastVisitDate;
    private String Diagnosis;
    private String Treatment;
    private boolean ifHospitalized;
    
    //A class as an attribute
    private HospitalizationDetails hospitalizationDetails;

    public ClinicalVisitHistory(LocalDate LastVisitDate, String Diagnosis, String Treatment, boolean ifHospitalized) {
        this.LastVisitDate = LastVisitDate;
        this.Diagnosis = Diagnosis;
        this.Treatment = Treatment;
        this.ifHospitalized = ifHospitalized;
        this.hospitalizationDetails = null; // Initialize hospitalizationDetails to null
    }

    //Method to add hospitalization details if the patient was hospitalized
    public void setHospitalizationDetails(HospitalizationDetails hospitalizationDetails) {
        if (this.hospitalizationDetails == null) {
            this.hospitalizationDetails = new HospitalizationDetails (
                hospitalizationDetails.getAdmissionDate(), 
                hospitalizationDetails.getDischargeDate(), 
                hospitalizationDetails.getDuration());
        } else {
            throw new IllegalStateException("Notice: Cannot add hospitalization details. Details already exist or is null.");
        }
    }

    public LocalDate getLastVisitDate() {
        return LastVisitDate;
    }

    public String getDiagnosis() {
        return Diagnosis;
    }

    public String getTreatment() {
        return Treatment;
    }

    public boolean getIfHospitalized() {
        return ifHospitalized;
    }

    public void setLastVisitDate(LocalDate LastVisitDate) {
        if (LastVisitDate == null) {
            throw new IllegalArgumentException("Last visit date cannot be null");
        }
        this.LastVisitDate = LastVisitDate;
    }

    public void setDiagnosis(String Diagnosis) {
        if (Diagnosis == null || Diagnosis.isEmpty()) {
            throw new IllegalArgumentException("Diagnosis cannot be null or empty");
        }
        this.Diagnosis = Diagnosis;
    }

    public void setTreatment(String Treatment) {
        if (Treatment == null || Treatment.isEmpty()) {
            throw new IllegalArgumentException("Treatment cannot be null or empty");
        }
        this.Treatment = Treatment;
    }
    
    public void setIfHospitalized(boolean ifHospitalized) {
        this.ifHospitalized = ifHospitalized;
    }

    public void displayVisitHistory() {
        System.out.println("Last Visit Date: " + LastVisitDate);
        System.out.println("Diagnosis: " + Diagnosis);
        System.out.println("Treatment: " + Treatment);
        System.out.println("Hospitalized: " + (ifHospitalized ? "Yes" : "No"));
        
        if (this.ifHospitalized && this.hospitalizationDetails != null) {
            this.hospitalizationDetails.displayHospitalizationDetails();
        }
    }
}
