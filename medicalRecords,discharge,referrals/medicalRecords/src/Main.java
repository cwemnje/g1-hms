import java.time.LocalDate;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        System.out.println("--- Scenario 1: Hospitalized is FALSE ---");
        ClinicalVisitHistory patient1 = new ClinicalVisitHistory(LocalDate.parse("2024-09-01"), "Flu", "Rest and hydration", false);
        patient1.displayVisitHistory();

        System.out.println("\n--- Scenario 2: Hospitalized is TRUE with hospitalization details ---");
        ClinicalVisitHistory patient2 = new ClinicalVisitHistory(LocalDate.parse("2024-09-01"), "Pneumonia", "Antibiotics and hospitalization", true);
        HospitalizationDetails hospitalizationDetails = new HospitalizationDetails(LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-10"), "10 days");
        patient2.setHospitalizationDetails(hospitalizationDetails);//because hospitalizationDetails is true, we can add hospitalization details
        patient2.displayVisitHistory();
    }
}
