package project.Model;

/**
 * The TripDataAndDuration class represents trip-related data, specifically focusing on the year, destination, 
 * the average length of trips, and the number of overnight trips for a particular destination and year.
 * 
 * This class is useful for handling trip statistics and duration information, enabling easy tracking 
 * and analysis of travel patterns over different years and destinations.
 */
public class TripDataAndDuration {
    private String year;
    private String destination;
    private Double averageLengthOfTrip;
    private int overnightTrips; 

    public TripDataAndDuration(String year, String destination, Double averageLengthOfTrip, int overnightTrips) {
        this.year = year;
        this.destination = destination;
        this.averageLengthOfTrip = averageLengthOfTrip;
        this.overnightTrips = overnightTrips;
    }

    // Getters and setters
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getAverageLengthOfTrip() {
        return averageLengthOfTrip;
    }

    public void setAverageLengthOfTrip(Double averageLengthOfTrip) {
        this.averageLengthOfTrip = averageLengthOfTrip;
    }

    public int getOvernightTrips() {
        return overnightTrips;
    }

    public void setOvernightTrips(int overnightTrips) {
        this.overnightTrips = overnightTrips;
    }
}

