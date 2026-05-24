/

// Parent Class
class User {
    // Encapsulation (private variables)
    private String username;
    private String password;
    private String role;

    // Constructor
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // Method for login
    public boolean login(String user, String pass) {
        return username.equals(user) && password.equals(pass);
    }

    // Method to display access
    public void accessSystem() {
        System.out.println(username + " has basic system access.");
    }
}

// Child Class 1
class Admin extends User {

    // Constructor
    public Admin(String username, String password) {
        super(username, password, "Admin");
    }

    // Polymorphism (Method Overriding)
    @Override
    public void accessSystem() {
        System.out.println("Admin has full access to the hospital system.");
    }
}

// Child Class 2
class Doctor extends User {

    public Doctor(String username, String password) {
        super(username, password, "Doctor");
    }

    @Override
    public void accessSystem() {
        System.out.println("Doctor can access patient medical records.");
    }
}

// Child Class 3
class Nurse extends User {

    public Nurse(String username, String password) {
        super(username, password, "Nurse");
    }

    @Override
    public void accessSystem() {
        System.out.println("Nurse can update patient care information.");
    }
}

// Main Class
public class HospitalManagementSystem {

    public static void main(String[] args) {

        // Creating objects
        User admin = new Admin("admin1", "1234");
        User doctor = new Doctor("doctor1", "abcd");
        User nurse = new Nurse("nurse1", "pass");

        // Authentication
        System.out.println("LOGIN TEST");

        if (admin.login("admin1", "1234")) {
            System.out.println("Admin Login Successful");
            admin.accessSystem();
        }

        System.out.println();

        if (doctor.login("doctor1", "abcd")) {
            System.out.println("Doctor Login Successful");
            doctor.accessSystem();
        }

        System.out.println();

        if (nurse.login("nurse1", "pass")) {
            System.out.println("Nurse Login Successful");
            nurse.accessSystem();
        }
    }
}