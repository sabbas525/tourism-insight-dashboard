package project.Model;

/**
 * The TouristDatabyPurpose class represents tourism data categorized by the purpose of travel.
 * It includes information such as the purpose's identifier, a description of the purpose, 
 * and the number of tourists associated with that purpose.
 */
public class TouristDatabyPurpose {
    private String purposeID;
    private String purpose;
    private Number trouristsNo;
    
    public TouristDatabyPurpose(String purposeID, String purpose, Number trouristsNo) {
        this.purposeID = purposeID;
        this.purpose = purpose;
        this.trouristsNo = trouristsNo;
    }

    public String getPurposeID() {
        return purposeID;
    }

    public String getPurpose() {
        return purpose;
    }

    public Number getTrouristsNo() {
        return trouristsNo;
    }
}
