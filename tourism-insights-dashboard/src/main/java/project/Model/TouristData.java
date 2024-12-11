package project.Model;

public class TouristData {
    private String regionID;
    private String regionNM;
    private int touristNo;

/**
 * The TouristData class encapsulates information about tourism statistics for a specific region.
 * It includes details such as the region's identifier, name, and the number of tourists recorded.
 */
    public TouristData(String regionID, String regionNM, int touristNo){
        this.regionID = regionID;
        this.regionNM = regionNM;
        this.touristNo = touristNo;
    }
    
    public String getRegionID() {
        return regionID;
    }
    
    public String getRegionName() {
        return regionNM;
    }
    
    public int getTrouristsNo() {
        return touristNo;
    }
}
