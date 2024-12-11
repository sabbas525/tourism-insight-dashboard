package project.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.core5.http.ParseException;
import project.Common.HttpGetWithEntity;
import project.Model.WeatherData;
import project.Common.PreferenceManager;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The WeatherService class is responsible for fetching and processing weather data from the Digitraffic API.
 * It provides functionality for retrieving weather data for specified stations, allowing you to make GET requests
 * to fetch relevant weather information such as temperature, humidity, and other meteorological conditions.
 *
 * Author: Waqas Hameed
 */
public class WeatherService {

    // Base URL for the weather data API
    private static final String API_URL = "https://tie.digitraffic.fi/api/weather/v1/stations/data";

    /**
     * Fetches weather data for the specified station IDs and date range.
     *
     * @param stationIds List of station IDs to fetch data for.
     * @param fromDate   Start date of the date range (inclusive).
     * @param toDate     End date of the date range (inclusive).
     * @return List of WeatherData objects containing weather data for the requested stations.
     */
    public List<WeatherData> fetchWeatherData(List<Integer> stationIds, LocalDateTime fromDate, LocalDateTime toDate) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        // Fetch location and date preferences from the PreferenceManager
        String location = PreferenceManager.getPreference("location", "Default Location");
        String date = PreferenceManager.getPreference("date", "2024-12-01");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Build the API URL with the required parameters
            String url = API_URL + "?lastUpdated=false";
            HttpGetWithEntity request = new HttpGetWithEntity(url);

            // Set necessary headers for the HTTP request
            request.addHeader("Accept", "application/json");
            request.addHeader("User-Agent", "Mozilla/5.0");

            // Prepare an empty entity (body) for the GET request
            HttpEntity emptyEntity = EntityBuilder.create()
                    .setText("")
                    .setContentType(ContentType.TEXT_PLAIN)
                    .build();
            request.setEntity(emptyEntity);

            // Execute the HTTP request and retrieve the response
            ClassicHttpResponse response = httpClient.executeOpen(null, request, null);
            int responseCode = response.getCode();
            System.out.println("Response Code: " + responseCode);

            // If the response code is not 200 (OK), log an error and return an empty list
            if (responseCode != 200) {
                System.err.println("Failed to fetch weather data: HTTP error code " + responseCode);
                return weatherDataList;
            }

            // Parse the JSON response into a JsonObject
            String jsonOutput = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject jsonResponse = new Gson().fromJson(jsonOutput, JsonObject.class);

            // Extract the stations array from the JSON response
            JsonArray stations = jsonResponse.getAsJsonArray("stations");
            if (stations == null) {
                System.err.println("No stations data found in the response.");
                return weatherDataList;
            }

            // Process each station in the response
            for (int i = 0; i < stations.size(); i++) {
                JsonObject station = stations.get(i).getAsJsonObject();
                int stationId = station.get("id").getAsInt();

                // If the station ID is not in the provided list of station IDs, skip it
                if (stationIds != null && !stationIds.isEmpty() && !stationIds.contains(stationId)) {
                    continue;
                }

                // Extract sensor values for the station
                JsonArray sensorValues = station.getAsJsonArray("sensorValues");
                if (sensorValues == null) {
                    continue;
                }

                // Initialize variables for weather data
                double airTemperature = Double.NaN;
                double windSpeed = Double.NaN;
                double precipitation = Double.NaN;
                String measuredTime = null;

                // Process each sensor's data
                for (int j = 0; j < sensorValues.size(); j++) {
                    JsonObject sensor = sensorValues.get(j).getAsJsonObject();
                    String name = sensor.has("name") ? sensor.get("name").getAsString() : null;
                    double value = sensor.has("value") ? sensor.get("value").getAsDouble() : Double.NaN;
                    measuredTime = sensor.has("measuredTime") ? sensor.get("measuredTime").getAsString() : null;

                    // Skip if the sensor data is incomplete or invalid
                    if (name == null || measuredTime == null || Double.isNaN(value)) {
                        continue;
                    }

                    // Map sensor data to weather parameters based on sensor name
                    switch (name) {
                        case "ILMA": // Air temperature
                            airTemperature = value;
                            break;
                        case "KESKITUULI": // Average wind speed
                            windSpeed = value;
                            break;
                        case "SADE": // Precipitation
                            precipitation = value;
                            break;
                        default:
                            break;
                    }
                }

                // Only process data if air temperature is valid and measured time is present
                if (!Double.isNaN(airTemperature) && measuredTime != null) {
                    try {
                        // Parse the measuredTime to LocalDateTime
                        LocalDateTime measurementDateTime = ZonedDateTime.parse(measuredTime)
                                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

                        // Check if the measurement time is within the specified date range
                        if (fromDate != null && toDate != null) {
                            if (measurementDateTime.isBefore(fromDate) || measurementDateTime.isAfter(toDate)) {
                                continue; // Skip data outside the date range
                            }
                        }

                        // Create a new WeatherData object with the fetched data
                        WeatherData data = new WeatherData(
                                stationId,
                                measuredTime,
                                airTemperature,
                                windSpeed,
                                precipitation
                        );
                        // Add the WeatherData object to the result list
                        weatherDataList.add(data);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing measured time: " + e.getMessage());
                        // Skip this data point if there's an error in parsing the time
                    }
                }
            }

        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Return the list of WeatherData objects
        return weatherDataList;
    }

    /**
     * Fetches a list of available weather station IDs from the API.
     *
     * @return List of station IDs.
     */
    public List<Integer> fetchWeatherStationIds() {
        List<Integer> stationIds = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Make a request to the weather API to get the list of stations
            HttpGetWithEntity request = new HttpGetWithEntity(API_URL);
            request.addHeader("Accept", "application/json");
            request.addHeader("User-Agent", "Mozilla/5.0");

            // Execute the HTTP request
            ClassicHttpResponse response = httpClient.executeOpen(null, request, null);
            if (response.getCode() == 200) {
                // Parse the JSON response to extract station IDs
                String jsonOutput = EntityUtils.toString(response.getEntity(), "UTF-8");
                JsonObject jsonResponse = new Gson().fromJson(jsonOutput, JsonObject.class);
                JsonArray stations = jsonResponse.getAsJsonArray("stations");
                for (int i = 0; i < stations.size(); i++) {
                    JsonObject station = stations.get(i).getAsJsonObject();
                    int stationId = station.get("id").getAsInt();
                    stationIds.add(stationId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return the list of station IDs
        return stationIds;
    }
}
