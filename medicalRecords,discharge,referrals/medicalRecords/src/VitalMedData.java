public class VitalMedData {
    private char bloodType;
    private char rhFactor;// Rhesus factor, either '+' or '-'
    private boolean haveAllergies;
    private AllergyDetail allergyDetail;

    public VitalMedData(char bloodType, char rhFactor, boolean haveAllergies) {
        this.bloodType = bloodType;
        this.rhFactor = rhFactor;
        this.haveAllergies = haveAllergies;
        this.allergyDetail = null;
    }

    //if has allergy is true...
    public void setAllergyDetail(AllergyDetail allergyDetail) {
        if (!haveAllergies) {
            throw new IllegalStateException("Cannot set allergy details when haveAllergies is false.");
        } else {
            this.allergyDetail = new AllergyDetail(
                allergyDetail.getAllergen(),
                allergyDetail.getReaction(),
                allergyDetail.getSeverity()
            );
        }
    }

    public char getBloodType() {
        return bloodType;
    }

    public char getRhFactor() {
        return rhFactor;
    }

    public boolean getHaveAllergies() {
        return haveAllergies;
    }

    public void setBloodType(char bloodType) {
        if (bloodType != 'A' && bloodType != 'B' && bloodType != 'AB' && bloodType != 'O') {
            throw new IllegalArgumentException("Invalid blood type. Must be A, B, AB, or O.");
        }
        this.bloodType = bloodType;
    }

    public void setRhFactor(char rhFactor) {
        if (rhFactor != '+' && rhFactor != '-') {
            throw new IllegalArgumentException("Invalid Rh factor. Must be '+' or '-'.");
        }
        this.rhFactor = rhFactor;
    }

    public void setHaveAllergies(boolean haveAllergies) {
        this.haveAllergies = haveAllergies;
    }   

    public void displayVitalMedData() {
        System.out.println("Blood Type: " + bloodType);
        System.out.println("Rh Factor: " + rhFactor);
        System.out.println("Have Allergies: " + (haveAllergies ? "Yes" : "No"));
        if (haveAllergies && allergyDetail != null) {
            System.out.println("Allergy Details:");
            allergyDetail.setAllergen(allergyDetail.getAllergen());
            allergyDetail.setReaction(allergyDetail.getReaction());
            allergyDetail.setSeverity(allergyDetail.getSeverity());
            System.out.println("Allergen: " + allergyDetail.getAllergen());
            System.out.println("Reaction: " + allergyDetail.getReaction());
            System.out.println("Severity: " + allergyDetail.getSeverity());
        }
    }

}
