public abstract class GuarantorInfo {
    private PersonalData guarantorPersonalData;

    public GuarantorInfo(PersonalData guarantorPersonalData) {
        this.guarantorPersonalData = guarantorPersonalData;
    }

    public PersonalData getGuarantorPersonalData() {
        return guarantorPersonalData;
    }


    public void setGuarantorPersonalData(PersonalData guarantorPersonalData) {
        this.guarantorPersonalData = guarantorPersonalData;
    }
    
    public abstract void GuarantorInfo(

    );
}
