public class PersonalRecords extends MedRecords{
    private String name;
    private String dateOfBirth;
    private String medicalHistory;

    public PersonalRecords(String name, String dateOfBirth, String medicalHistory) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.medicalHistory = medicalHistory;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }
}