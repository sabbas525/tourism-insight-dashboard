package project.ServiceFacade;

import java.util.List;

import project.Common.IDataService;
import project.Common.ISpecificKeywiseDataService;
import project.Model.TouristData;
import project.Model.TouristDatabyPurpose;
import project.Model.TouristYearData;
import project.Service.ForeignVisitorsByPurposeDataService;
import project.Service.ForeignVisitorsDataService;
import project.Service.ForeignVisitorsYearWiseDataService;

/**
 * The HomeServiceFacade class is a facade that simplifies access to various data services related to foreign visitor statistics.
 * It acts as a central point for interacting with different data services that handle various aspects of foreign visitor data,
 * such as total visitor counts, year-wise data, and purpose of visit data.
 * 
 * By using the HomeServiceFacade, clients of the application can interact with a single service class
 * without needing to manage each individual data service separately.
 * 
 */
public class HomeServiceFacade{
    private IDataService<TouristData> foreignVisitorsDataService;
    private ISpecificKeywiseDataService<TouristYearData> foreignVisitorsYearWiseDataService;
    private IDataService<TouristDatabyPurpose> foreignVisitorsByPurposeDataService;


    public HomeServiceFacade() {
        this.foreignVisitorsDataService = new ForeignVisitorsDataService();
        this.foreignVisitorsYearWiseDataService = new ForeignVisitorsYearWiseDataService();
        this.foreignVisitorsByPurposeDataService = new ForeignVisitorsByPurposeDataService();
    }

    public List<TouristData> getTouriseDataList(){
        return foreignVisitorsDataService.processData();
    }

    public List<TouristData> getLocationWiseTouriseDataList(List<TouristData> touristDataList, String selectedRegion){
        return foreignVisitorsDataService.filterData(touristDataList,selectedRegion);
    }

    public List<TouristYearData> getLocationAndYearWiseTouriseDataList(String selectedRegion){
        return foreignVisitorsYearWiseDataService.processData(selectedRegion);
    }
    
    public List<TouristDatabyPurpose> getTourisePurposeDataList(){
        return foreignVisitorsByPurposeDataService.processData();
    }

    public List<TouristDatabyPurpose> getPurposeWiseTouriseDataList(List<TouristDatabyPurpose> touristDatabyPurposeList, String filterStr){
        return foreignVisitorsByPurposeDataService.filterData(touristDatabyPurposeList,filterStr);
    }
}
