public class AllergyDetail {
    private String allergen;//the substance that causes the allergic reaction
    private String reaction;
    private String severity;//high, medium, low

    public AllergyDetail(String allergen, String reaction, String severity) {
        this.allergen = allergen;
        this.reaction = reaction;
        this.severity = severity;
    }

    public String getAllergen() {
        return allergen;
    }

    public String getReaction() {
        return reaction;
    }

    public String getSeverity() {
        return severity;
    }

    public void setAllergen(String allergen) {
        if (allergen == null || allergen.isEmpty()) {
            throw new IllegalArgumentException("Allergen cannot be null or empty");
        }
        this.allergen = allergen;
    }

    public void setReaction(String reaction) {
        if (reaction == null || reaction.isEmpty()) {
            throw new IllegalArgumentException("Reaction cannot be null or empty");
        }
        this.reaction = reaction;
    }

    public void setSeverity(String severity) {
        if (severity == null || severity.isEmpty()) {
            throw new IllegalArgumentException("Severity cannot be null or empty");
        }
        this.severity = severity;
    }

}
