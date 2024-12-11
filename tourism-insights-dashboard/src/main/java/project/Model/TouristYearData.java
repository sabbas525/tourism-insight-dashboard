package project.Model;

/**
 * The TouristYearData class represents tourism data aggregated by year. 
 * It includes the year and the corresponding number of tourists for that year.
 */
public class TouristYearData {
    private String year;
    private Number trouristsNo;
    
    public TouristYearData(String year, Number trouristsNo) {
        this.year = year;
        this.trouristsNo = trouristsNo;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setTrouristsNo(Number trouristsNo) {
        this.trouristsNo = trouristsNo;
    }

    public Number getTrouristsNo() {
        return trouristsNo;
    }
   
}
