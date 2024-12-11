package project.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.apache.hc.core5.http.ParseException;
import project.Common.HttpGetWithEntity;
import project.Model.TrafficData;
import project.Common.PreferenceManager;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The TrafficService class is responsible for fetching and processing traffic data
 * from the Digitraffic API. It provides methods to retrieve traffic data based on
 * specific station IDs and time ranges.
 */
public class TrafficService {

    // Base URL for the API request
    private static final String API_URL = "https://tie.digitraffic.fi/api/tms/v1/stations/data?lastUpdated=false";

    /**
     * Fetches traffic data for specific station IDs within a given time range.
     *
     * @param stationIds List of station IDs to filter traffic data.
     * @param fromDate  Start date and time for filtering data.
     * @param toDate    End date and time for filtering data.
     * @return A list of TrafficData objects containing the fetched data.
     */
    public List<TrafficData> fetchTrafficData(List<Integer> stationIds, LocalDateTime fromDate, LocalDateTime toDate) {
        List<TrafficData> trafficDataList = new ArrayList<>();

        // Retrieve user preferences for location and date
        String location = PreferenceManager.getPreference("location", "Default Location");
        String date = PreferenceManager.getPreference("date", "2024-12-01");

        // HTTP client setup
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = API_URL;
            HttpGetWithEntity request = new HttpGetWithEntity(url);

            // Set request headers
            request.addHeader("Accept", "application/json");
            request.addHeader("User-Agent", "Mozilla/5.0");

            // Set empty entity (no data in body)
            HttpEntity emptyEntity = EntityBuilder.create()
                    .setText("")
                    .setContentType(ContentType.TEXT_PLAIN)
                    .build();
            request.setEntity(emptyEntity);

            // Execute the HTTP request
            ClassicHttpResponse response = httpClient.executeOpen(null, request, null);

            int responseCode = response.getCode();
            System.out.println("Response Code: " + responseCode);

            // Check if the response is successful
            if (responseCode != 200) {
                System.err.println("Failed to fetch traffic data: HTTP error code " + responseCode);
                return trafficDataList;
            }

            // Parse JSON response
            String jsonOutput = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject jsonResponse = new Gson().fromJson(jsonOutput, JsonObject.class);

            // Get the stations data from the response
            JsonArray stations = jsonResponse.getAsJsonArray("stations");
            if (stations == null) {
                System.err.println("No stations data found in the response.");
                return trafficDataList;
            }

            // Iterate through the stations and process the sensor values
            for (int i = 0; i < stations.size(); i++) {
                JsonObject station = stations.get(i).getAsJsonObject();
                int stationId = station.get("id").getAsInt();

                // Skip stations that are not in the provided stationIds list
                if (stationIds != null && !stationIds.isEmpty() && !stationIds.contains(stationId)) {
                    continue;
                }

                JsonArray sensorValues = station.getAsJsonArray("sensorValues");
                if (sensorValues == null) {
                    continue;
                }

                // Initialize variables to hold traffic data values
                double volume = Double.NaN;
                double speed = Double.NaN;
                String measuredTime = null;

                // Process each sensor value in the station
                for (int j = 0; j < sensorValues.size(); j++) {
                    JsonObject sensor = sensorValues.get(j).getAsJsonObject();
                    String name = sensor.has("name") ? sensor.get("name").getAsString() : null;
                    double value = sensor.has("value") ? sensor.get("value").getAsDouble() : Double.NaN;
                    measuredTime = sensor.has("measuredTime") ? sensor.get("measuredTime").getAsString() : null;

                    // Skip invalid sensor data
                    if (name == null || measuredTime == null || Double.isNaN(value)) {
                        continue;
                    }

                    // Assign values based on sensor name
                    switch (name) {
                        case "OHITUKSET_60MIN_KIINTEA_SUUNTA1": // Volume
                            volume = value;
                            break;
                        case "NOPEUS_KIINTEA_SUUNTA1": // Speed
                            speed = value;
                            break;
                        default:
                            break;
                    }
                }

                // If measured time is valid, process the data
                if (measuredTime != null) {
                    try {
                        // Parse the measured time to LocalDateTime
                        LocalDateTime measurementDateTime = ZonedDateTime.parse(measuredTime)
                                .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

                        // Filter data based on the provided date range
                        if (fromDate != null && toDate != null) {
                            if (measurementDateTime.isBefore(fromDate) || measurementDateTime.isAfter(toDate)) {
                                continue;
                            }
                        }

                        // Create TrafficData object and add it to the list
                        TrafficData data = new TrafficData(
                                stationId,
                                measuredTime,
                                volume,
                                speed
                        );
                        trafficDataList.add(data);

                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing measured time: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException occurred while fetching traffic data: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return trafficDataList;
    }

    /**
     * Fetches the IDs of all traffic stations.
     *
     * @return A list of station IDs.
     */
    public List<Integer> fetchTrafficStationIds() {
        List<Integer> stationIds = new ArrayList<>();

        // HTTP client setup
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGetWithEntity request = new HttpGetWithEntity(API_URL);
            request.addHeader("Accept", "application/json");
            request.addHeader("User-Agent", "Mozilla/5.0");

            // Execute the HTTP request
            ClassicHttpResponse response = httpClient.executeOpen(null, request, null);
            if (response.getCode() == 200) {
                String jsonOutput = EntityUtils.toString(response.getEntity(), "UTF-8");
                JsonObject jsonResponse = new Gson().fromJson(jsonOutput, JsonObject.class);

                // Get the stations data from the response
                JsonArray stations = jsonResponse.getAsJsonArray("stations");
                for (int i = 0; i < stations.size(); i++) {
                    JsonObject station = stations.get(i).getAsJsonObject();
                    int stationId = station.get("id").getAsInt();
                    stationIds.add(stationId); // Add station ID to the list
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stationIds;
    }
}
