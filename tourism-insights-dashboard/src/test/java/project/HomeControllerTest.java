package project;

import project.Model.TouristData;
import project.Model.TouristDatabyPurpose;
import project.Model.TouristYearData;
import project.ServiceFacade.HomeServiceFacade;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * HomeControllerTest is a test class for testing the functionality of the HomeController and its integration with the
 * HomeServiceFacade class, which aggregates multiple services related to tourism data.
 * 
 * It also manages the filtering and display of statistics based on the user's selected filters.
 * 
 */
@TestInstance(Lifecycle.PER_CLASS)
public class HomeControllerTest {

    private HomeServiceFacade homeService;

    @BeforeEach
    public void setUp() throws Exception {
        homeService = new HomeServiceFacade();
    }
 
    @Test
    void testForeignVisitorsDataService() {
        List<TouristData> data = homeService.getTouriseDataList();
        List<TouristData> filteredData = homeService.getLocationWiseTouriseDataList(data,"Tampere");

        assertNotNull(data);
        assertEquals(2, filteredData.size());

        assertEquals("Tampere", filteredData.get(1).getRegionName());
        assertEquals("KU837", filteredData.get(1).getRegionID());
    }

    @Test
    void testForeignVisitorsYearWiseDataService() {
        
        List<TouristYearData> data = homeService.getLocationAndYearWiseTouriseDataList("KU091");//Helsinki

        assertNotNull(data);
        assertFalse(data.isEmpty());
    }

    @Test
    void testForeignVisitorsByPurposeDataService() {
        List<TouristDatabyPurpose> data = homeService.getTourisePurposeDataList();
        List<TouristDatabyPurpose> filteredData = homeService.getPurposeWiseTouriseDataList(data,"1,6");
        assertNotNull(data);
        assertEquals(2, filteredData.size());
        
        assertEquals("Personal", filteredData.get(0).getPurpose());
        assertEquals("Work", filteredData.get(1).getPurpose());

    }

    @Test
    void testForeignVisitorsByPersonalPurposeDataService() {
        List<TouristDatabyPurpose> data = homeService.getTourisePurposeDataList();
        List<TouristDatabyPurpose> filteredData = homeService.getPurposeWiseTouriseDataList(data,"2,3,4,5");

        assertNotNull(data);
        assertEquals(4, filteredData.size());

        assertEquals("Holiday", filteredData.get(0).getPurpose());
        assertEquals("Visit", filteredData.get(1).getPurpose());
        assertEquals("Studying", filteredData.get(2).getPurpose());
        assertEquals("Other personal reasons", filteredData.get(3).getPurpose());
    }

    @Test
    void testForeignVisitorsByWorkPurposeDataService() {
        List<TouristDatabyPurpose> data = homeService.getTourisePurposeDataList();
        List<TouristDatabyPurpose> filteredData = homeService.getPurposeWiseTouriseDataList(data,"7,8,9,10");

        assertNotNull(data);
        assertEquals(4, filteredData.size());

        assertEquals("Business trip", filteredData.get(0).getPurpose());
        assertEquals("Conference or fair", filteredData.get(1).getPurpose());
        assertEquals("Finnish employer", filteredData.get(2).getPurpose());
        assertEquals("Other work reasons", filteredData.get(3).getPurpose());
    }

}
