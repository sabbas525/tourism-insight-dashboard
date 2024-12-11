package project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.Common.PreferenceManager;
import project.Controller.WeatherController;
import project.Model.TrafficData;
import project.Model.WeatherData;
import project.Service.TrafficService;
import project.Service.WeatherService;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
class WeatherControllerTest {

    private final WeatherService weatherService = new WeatherService();
    private final TrafficService trafficService = new TrafficService();

    @Test
    void testFetchWeatherData() {
        // Example station IDs and date range
        List<Integer> stationIds = List.of(1001, 1002, 1003); // Replace with valid station IDs if needed
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);  // 1 day ago
        LocalDateTime toDate = LocalDateTime.now(); // Current time

        // Call the fetchWeatherData method
        List<WeatherData> weatherDataList = weatherService.fetchWeatherData(stationIds, fromDate, toDate);

        // Assert the result is not empty and contains valid data
        assertNotNull(weatherDataList);
        assertTrue(weatherDataList.size() > 0, "Weather data list should not be empty");

        // Check the first element for valid properties (e.g., stationId and temperature)
        WeatherData firstData = weatherDataList.get(0);
        assertNotNull(firstData.getStationId(), "Station ID should not be null");
        assertNotNull(firstData.getMeasurementTime(), "Measurement time should not be null");
        assertTrue(firstData.getAirTemperature() != Double.NaN, "Air temperature should not be NaN");
    }

    @Test
    void testFetchWeatherStationIds() {
        // Call the method to get all station IDs
        List<Integer> stationIds = weatherService.fetchWeatherStationIds();

        // Assert that the list is not empty
        assertNotNull(stationIds);
        assertTrue(stationIds.size() > 0, "Weather station IDs list should not be empty");

        // Optionally, assert that the first station ID is a valid integer (not negative)
        assertTrue(stationIds.get(0) > 0, "The station ID should be greater than 0");
    }
    @Test
    void testFetchTrafficData() {
        // Example station IDs and date range
        List<Integer> stationIds = List.of(20001, 20002, 20005); // Replace with valid station IDs if needed
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);  // 1 day ago
        LocalDateTime toDate = LocalDateTime.now(); // Current time

        // Call the fetchTrafficData method
        List<TrafficData> trafficDataList = trafficService.fetchTrafficData(stationIds, fromDate, toDate);

        // Assert the result is not empty and contains valid data
        assertNotNull(trafficDataList);
        assertTrue(trafficDataList.size() > 0, "Traffic data list should not be empty");

        // Check the first element for valid properties (e.g., stationId and volume)
        TrafficData firstData = trafficDataList.get(0);
        assertNotNull(firstData.getStationId(), "Station ID should not be null");
        assertNotNull(firstData.getMeasurementTime(), "Measurement time should not be null");
        assertTrue(firstData.getVolume() != Double.NaN, "Volume should not be NaN");
    }

    @Test
    void testFetchTrafficStationIds() {
        // Call the method to get all station IDs
        List<Integer> stationIds = trafficService.fetchTrafficStationIds();

        // Assert that the list is not empty
        assertNotNull(stationIds);
        assertTrue(stationIds.size() > 0, "Traffic station IDs list should not be empty");

        // Optionally, assert that the first station ID is a valid integer (not negative)
        assertTrue(stationIds.get(0) > 0, "The station ID should be greater than 0");
    }
}