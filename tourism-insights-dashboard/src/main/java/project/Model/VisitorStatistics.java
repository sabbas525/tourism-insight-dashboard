package project.Model;

/**
 * The VisitorStatistics class represents statistical data for visitors, including the year, 
 * age group, gender, trip type, and the percentage of visitors in each category.
 * 
 * This class is used to store and analyze visitor data, providing insights based on demographics 
 * such as age group, gender, and trip type for a particular year.
 */
public class VisitorStatistics {
    private String year;
    private String ageGroup;
    private String gender;
    private String tripType;
    private double percentage;

    // Constructors
    public VisitorStatistics(String year, String ageGroup, String gender, String tripType, Double percentage) {
        this.year = year;
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.tripType = tripType;
        this.percentage = percentage;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String type) {
        this.tripType = type;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
