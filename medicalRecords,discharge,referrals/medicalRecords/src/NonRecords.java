public class NonRecords extends MedRecords {
    private PersonalData patient;
    private String insuranceCompany;
    private BillingHistory billingHistory;
    private GuarantorInfo guarantorInfo;//information about the person responsible for payment of the bills for the patient, especially for minors

    public NonRecords(PersonalData patient, String insuranceCompany, BillingHistory billingHistory, GuarantorInfo guarantorInfo) {
        this.patient = patient;
        this.insuranceCompany = insuranceCompany;
        this.billingHistory = billingHistory;
        this.guarantorInfo = guarantorInfo;
    }

    public PersonalData getPersonalData() {
        return patient;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public BillingHistory getBillingHistory() {
        return billingHistory;
    }

    public GuarantorInfo getGuarantorInfo() {
        return guarantorInfo;
    }

    public void setPersonalData(PersonalData patient) {
        this.patient = new PersonalData (
            patient.getId(),
            patient.getName(), 
            patient.getDateOfBirth(), 
            patient.getGender()
        );
    }

    public void setInsuranceCompany(String insuranceCompany) {
        if (insuranceCompany == null || insuranceCompany.isEmpty()) {
            throw new IllegalArgumentException("Insurance company cannot be null or empty");
        }
        this.insuranceCompany = insuranceCompany;
    }

    public void setBillingHistory(BillingHistory billingHistory) {
        this.billingHistory = billingHistory;
    }

    public void setGuarantorInfo(GuarantorInfo guarantorInfo) {
        this.guarantorInfo = guarantorInfo;
    }
}