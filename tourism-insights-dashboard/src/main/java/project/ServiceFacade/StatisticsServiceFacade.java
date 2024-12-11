package project.ServiceFacade;

import java.util.List;
import project.Common.IDataService;
import project.Model.TripDataAndDuration;
import project.Model.TripStatistics;
import project.Model.VisitorStatistics;
import project.Service.TripsAndDurationDataService;
import project.Service.TripsByDestinationDataService;
import project.Service.VisitorStatisticsDataService;

/**
 * The StatisticsServiceFacade class is a facade that provides a unified interface for accessing various data services 
 * related to statistical information on trips and visitors. It encapsulates the interaction with different services 
 * that handle trip statistics, visitor statistics, and trip duration data.
 * 
 * By using the StatisticsServiceFacade, clients can access the statistical data from these different services 
 * through a single interface without the need to manage each individual service separately.
 * 
 */
public class StatisticsServiceFacade{
    private IDataService<TripStatistics> tripsByDestinationService;
    private IDataService<VisitorStatistics> visitorStatService;
    private IDataService<TripDataAndDuration> tripsAndDurationService;

    public StatisticsServiceFacade() {
        this.tripsByDestinationService = new TripsByDestinationDataService();
        this.visitorStatService = new VisitorStatisticsDataService();
        this.tripsAndDurationService = new TripsAndDurationDataService();
    }

    public List<TripStatistics> getTripsByDestination() {
        return tripsByDestinationService.processData();
    }

    public List<VisitorStatistics> getVisitorStatistics() {
        return visitorStatService.processData();
    }

    public List<TripDataAndDuration> getTripsAndDuration() {
        return tripsAndDurationService.processData();
    }

    public List<TripStatistics> getTripsBySeason(List<TripStatistics> tripDataList, String selectedSeason) {
        return tripsByDestinationService.filterData(tripDataList, selectedSeason);
    }

    public List<VisitorStatistics> getVisitorsByTripType(List<VisitorStatistics> visitorDataList, String tripType) {
        return visitorStatService.filterData(visitorDataList, tripType);
    }

    public List<TripDataAndDuration> getTripsByYear(List<TripDataAndDuration> tripDataList, String selectedYear) {
        return tripsAndDurationService.filterData(tripDataList, selectedYear);
    }
}
