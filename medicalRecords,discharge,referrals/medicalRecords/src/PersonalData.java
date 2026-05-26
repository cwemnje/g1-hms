import java.time.LocalDate;

public class PersonalData {
    private int Id;
    private String name;
    private LocalDate dateOfBirth;
    private String Gender;

    public PersonalData(int Id, String name, LocalDate dateOfBirth, String Gender) {
        this.Id = Id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.Gender = Gender;
    }

    public int getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null");
        }
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String Gender) {
        if (Gender == null || Gender.isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
    }
        this.Gender = Gender;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public void displayPersonalData() {
        System.out.println("Patient ID: " + Id);
        System.out.println("Name: " + name);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Gender: " + Gender);
    }
}