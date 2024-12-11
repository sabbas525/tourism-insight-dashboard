package project.Model;

public class TripStatistics {
    private String destination;
    private Double tripCount;
    private String season;

/**
 * The TripStatistics class represents the statistical data of trips, including the destination, 
 * season, and the number of trips associated with each destination for a given season.
 * 
 * This class is used to track and analyze the trip data for different destinations, seasons, 
 * and the associated trip counts.
 */
    public TripStatistics(String destination, String season, Double tripCount) {
        this.destination = destination;
        this.season = season;
        this.tripCount = tripCount; 
    }

    public String getDestination() {
        return destination;
    }

    public Double getTripCount() {
        return tripCount;
    }

    public void setTripCount(Double tripCount) {
        this.tripCount = tripCount;
    }

    public String getSeason() {
        return season;
    }
}
