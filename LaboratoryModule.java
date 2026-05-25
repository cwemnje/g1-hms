import java.util.ArrayList;
import java.util.List;

// PATIENT - stores basic patient information
class Patient {
    String id;
    String name;
    int    age;
    String gender;

    // Constructor: called when creating a new Patient object
    Patient(String id, String name, int age, String gender) {
        this.id     = id;
        this.name   = name;
        this.age    = age;
        this.gender = gender;
    }
}

// LAB TEST - describes what test will be done and its cost
class LabTest {
    String testCode;   
    String testName;   
    double price;

    LabTest(String testCode, String testName, double price) {
        this.testCode = testCode;
        this.testName = testName;
        this.price    = price;
    }
}

// RESULT - one row in the lab report
// e.g"Haemoglobin, 13.5, g/dL, Normal"
class Result {
    String parameter;      // what was measured
    String value;          // the measured value
    String unit;           // unit of measurement
    String referenceRange; // what's considered normal
    boolean isAbnormal;    // true if result is outside normal range

    Result(String parameter, String value, String unit,
           String referenceRange, boolean isAbnormal) {
        this.parameter      = parameter;
        this.value          = value;
        this.unit           = unit;
        this.referenceRange = referenceRange;
        this.isAbnormal     = isAbnormal;
    }
}

// LAB ORDER - the main record that tracks everything
// from the moment a test is requested to when results are out 
class LabOrder {
    String       orderId;
    Patient      patient;
    LabTest      test;
    String       doctorName;

    // Status tracks where the order is in the process
    String       status;      
    String       sampleType;  
    String       collectedBy; 

    List<Result> results = new ArrayList<>();
    String       remarks;     

    // Constructor sets the initial state when an order is created
    LabOrder(String orderId, Patient patient, LabTest test, String doctorName) {
        this.orderId    = orderId;
        this.patient    = patient;
        this.test       = test;
        this.doctorName = doctorName;
        this.status     = "Pending";  // always starts as Pending
    }
}

// LABORATORY - the main class that handles all lab operations
class Laboratory {

    // We store all orders in a list
    List<LabOrder> allOrders = new ArrayList<>();

    // Counter to auto-generate order IDs like 001,002 
    int orderCount = 1;

    // Create a new lab order 
    LabOrder createOrder(Patient patient, LabTest test, String doctorName) {
        // Generate a unique ID for this order
        String orderId = String.format("%03d", orderCount);
        orderCount++;

        // Create the order and add it to our list
        LabOrder order = new LabOrder(orderId, patient, test, doctorName);
        allOrders.add(order);

        System.out.println("✔ Order created: " + orderId +
                " | Test: " + test.testName +
                " | Patient: " + patient.name);
        return order;
    }

    // Collect the sample from the patient
    void collectSample(LabOrder order, String sampleType, String techName) {
        order.sampleType  = sampleType;
        order.collectedBy = techName;
        order.status      = "Sample Collected";

        System.out.println("✔ Sample collected for " + order.orderId +
                " | Type: " + sampleType + " | By: " + techName);
    }

    // Start processing the sample in the lab
    void processOrder(LabOrder order) {
        order.status = "In Progress";
        System.out.println("✔ Processing started for " + order.orderId);
    }

    // Enter the test results
    void enterResults(LabOrder order, List<Result> results, String remarks) {
        order.results = results;
        order.remarks = remarks;
        order.status  = "Done";

        System.out.println("✔ Results entered for " + order.orderId +
                " | Status: Done");
    }

    // Print a clean lab report for the patient
    void printReport(LabOrder order) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("           LABORATORY REPORT              ");
        System.out.println("==========================================");
        System.out.println("Order ID   : " + order.orderId);
        System.out.println("Patient    : " + order.patient.name);
        System.out.println("Age/Gender : " + order.patient.age + " / " + order.patient.gender);
        System.out.println("Test       : " + order.test.testName + " (" + order.test.testCode + ")");
        System.out.println("Price      : GHS " + order.test.price);
        System.out.println("Doctor     : " + order.doctorName);
        System.out.println("Sample     : " + order.sampleType + " (by " + order.collectedBy + ")");
        System.out.println("Status     : " + order.status);
        System.out.println("------------------------------------------");
        System.out.println("RESULTS:");
        System.out.printf("  %-22s %-10s %-8s %-15s %s%n",
                "Parameter", "Value", "Unit", "Normal Range", "Flag");
        System.out.println("  " + "-".repeat(65));

        for (Result r : order.results) {
            // Print *** next to abnormal results so they stand out
            String flag = r.isAbnormal ? "*** ABNORMAL" : "Normal";
            System.out.printf("  %-22s %-10s %-8s %-15s %s%n",
                    r.parameter, r.value, r.unit, r.referenceRange, flag);
        }

        System.out.println("------------------------------------------");
        System.out.println("Remarks: " + (order.remarks != null ? order.remarks : "None"));
        System.out.println("==========================================");
        System.out.println();
    }

    // EXTRA: Show all orders in a simple table (the worklist)
    void printWorkList() {
        System.out.println();
        System.out.println("========== LAB WORKLIST ==========");
        System.out.printf("%-10s %-15s %-25s %-20s%n",
                "Order ID", "Patient", "Test", "Status");
        System.out.println("-".repeat(72));

        for (LabOrder o : allOrders) {
            System.out.printf("%-10s %-15s %-25s %-20s%n",
                    o.orderId, o.patient.name, o.test.testName, o.status);
        }
        System.out.println("==================================");
        System.out.println();
    }
}

// MAIN - runs the whole demo from start to finish
public class LaboratoryModule {

    public static void main(String[] args) {

        System.out.println("=== HOSPITAL LAB MODULE - DEMO ===\n");

        // Create the lab 
        Laboratory lab = new Laboratory();


        // Create some patients 
        Patient alice = new Patient("P001", "Alice Mensah",  34, "Female");
        Patient kwame = new Patient("P002", "Kwame Asante",  52, "Male");


        // Define the tests
        LabTest fbc = new LabTest("FBC",  "Full Blood Count",    45.00);
        LabTest fbg = new LabTest("FBG",  "Fasting Blood Glucose", 15.00);
        LabTest kft = new LabTest("KFT",  "Kidney Function Test",  60.00);

        //  ORDER 1: Alice needs a Full Blood Count
        System.out.println("--- ORDER 1 ---");

        // Doctor requests the test
        LabOrder order1 = lab.createOrder(alice, fbc, "Dr. Owusu");

        // Lab tech collects blood sample
        lab.collectSample(order1, "Blood", "Tech Kofi");

        // Lab starts processing
        lab.processOrder(order1);

        // Enter results
        List<Result> fbcResults = new ArrayList<>();
        fbcResults.add(new Result("WBC",         "11.2", "x10³/µL", "4.0 - 11.0",  true));  
        fbcResults.add(new Result("Haemoglobin", "13.5", "g/dL",    "12.0 - 16.0", false));  
        fbcResults.add(new Result("Platelets",   "420",  "x10³/µL", "150 - 400",   true));   
        fbcResults.add(new Result("RBC",         "4.8",  "x10⁶/µL", "3.8 - 5.2",   false));  

        lab.enterResults(order1, fbcResults, "Mild high WBC. Repeat in 2 weeks.");

        // Print the report
        lab.printReport(order1);

        //  ORDER 2: Kwame needs a Blood Glucose test
        System.out.println("--- ORDER 2 ---");

        LabOrder order2 = lab.createOrder(kwame, fbg, "Dr. Acheampong");
        lab.collectSample(order2, "Blood", "Tech Abena");
        lab.processOrder(order2);

        List<Result> fbgResults = new ArrayList<>();
        fbgResults.add(new Result("Fasting Blood Glucose", "19.4", "mmol/L", "3.9 - 6.1", true)); // very high!

        lab.enterResults(order2, fbgResults, "CRITICAL: Very high sugar. Inform doctor immediately!");
        lab.printReport(order2);
        //  ORDER 3: Alice also needs Kidney Function
        //  (still pending - not done yet)
        System.out.println("--- ORDER 3 (Still Pending) ---");
        LabOrder order3 = lab.createOrder(alice, kft, "Dr. Owusu");
        
        System.out.println("Order placed but sample not collected yet.\n");
        lab.printWorkList();
    }
}