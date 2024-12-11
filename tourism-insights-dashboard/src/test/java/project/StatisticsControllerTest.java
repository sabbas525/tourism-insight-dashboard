package project;

import project.Model.TripDataAndDuration;
import project.Model.TripStatistics;
import project.Model.VisitorStatistics;
import project.ServiceFacade.StatisticsServiceFacade;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * StatisticsControllerTest is a test class designed to validate the functionality of the StatisticsController and its
 * interaction with the StatisticsServiceFacade. It ensures that the data filtering and retrieval operations work as expected.
 */
@TestInstance(Lifecycle.PER_CLASS)
public class StatisticsControllerTest {

    private StatisticsServiceFacade statisticsServiceFacade;

    @BeforeEach
    public void setUp() throws Exception {
        statisticsServiceFacade = new StatisticsServiceFacade();
    }
 
    @Test
    void testFilterData() {
    // Mock data
        List<TripStatistics> trips = List.of(
            new TripStatistics("Tampere", "Spring", 100.0),
            new TripStatistics("Helsinki", "Summer", 150.0)
        );

        List<TripStatistics> filteredData = statisticsServiceFacade.getTripsBySeason(trips, "Spring");


        assertNotNull(filteredData);
        assertEquals(1, filteredData.size());

        assertEquals("Tampere", filteredData.get(0).getDestination());
        assertEquals(100.0, filteredData.get(0).getTripCount());
    }

    @Test
    void testFilterDataNoMatch() {
        List<VisitorStatistics> visitorDataList = List.of(
            new VisitorStatistics("2023", "20-30", "Male", "Business", 50.0),
            new VisitorStatistics("2022", "30-40", "Female", "Leisure", 40.0)
        );

        // Act
        List<VisitorStatistics> filteredData = statisticsServiceFacade.getVisitorsByTripType(visitorDataList, "Vacation");

        // Assert
        assertNotNull(filteredData);
        assertTrue(filteredData.isEmpty());
    }

    @Test
    void testGetTripsByDestinationEmptyData() {
        StatisticsServiceFacade serviceFacade = new StatisticsServiceFacadeStub();
        List<TripStatistics> trips = serviceFacade.getTripsByDestination();

        // Assert that the list is empty
        assertTrue(trips.isEmpty());
    }

    @Test
    void testTripStatisticsModel() {
        TripDataAndDuration tripDuration = new TripDataAndDuration("2022", "Destination1", 2.0, 450 );

        // Test getters
        assertEquals("Destination1", tripDuration.getDestination());
        assertEquals(450, tripDuration.getOvernightTrips());
        assertEquals("2022", tripDuration.getYear());
    }

    static class StatisticsServiceFacadeStub extends StatisticsServiceFacade {
        @Override
        public List<TripStatistics> getTripsByDestination() {
            return List.of(); // Return empty list for edge case
        }
    }

}
