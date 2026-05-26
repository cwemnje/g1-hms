public class OfficialRecords extends MedRecords {
    private PersonalData patient;
    private ClinicalVisitHistory clinicalVisitHistory;
    private VitalMedData vitalMedData;

    public OfficialRecords(PersonalData patient, ClinicalVisitHistory clinicalVisitHistory, VitalMedData vitalMedData) {
        this.patient = patient;
        this.clinicalVisitHistory = clinicalVisitHistory;
        this.vitalMedData = vitalMedData;
    }

    public PersonalData getPersonalData() {
        return patient;
    }

    public ClinicalVisitHistory getClinicalVisitHistory() {
        return clinicalVisitHistory;
    }

    public VitalMedData getVitalMedData() {
        return vitalMedData;
    }

    public void setPersonalData(PersonalData patient) {
        this.patient = new PersonalData (
            patient.getId(),
            patient.getName(), 
            patient.getDateOfBirth(), 
            patient.getGender()
        );
    }

    public void setClinicalVisitHistory(ClinicalVisitHistory clinicalVisitHistory) {
        this.clinicalVisitHistory = new ClinicalVisitHistory(
            clinicalVisitHistory.getLastVisitDate(), 
            clinicalVisitHistory.getDiagnosis(), 
            clinicalVisitHistory.getTreatment(), 
            clinicalVisitHistory.getIfHospitalized()    
        );
    }

    public void setVitalMedData(VitalMedData vitalMedData) {
        this.vitalMedData = new VitalMedData(
            vitalMedData.getBloodType(),
            vitalMedData.getRhFactor(),
            vitalMedData.getHaveAllergies()
        );
    }

    public void displayOfficialRecords() {
        System.out.println();
        displayPersonalData();
        System.out.println();
        clinicalVisitHistory.displayVisitHistory();
        System.out.println();
        vitalMedData.displayVitalMedData();
    }
}