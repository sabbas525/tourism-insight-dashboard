package project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import project.Common.IDataService;
import project.Model.TripDataAndDuration;

/**
 * The TripsAndDurationDataService class is a service that extends the StatisticsFinlandDataService
 * and implements the IDataService interface to retrieve and process trips and duration data from the StatisticsFinland API.
 * 
 * It interacts with the StatisticsFinland API to fetch data on trips and their duration. It uses a predefined 
 * JSON query to retrieve the data, then processes the API response to extract relevant statistics on trips and durations, 
 * mapping the results into a list of TripDataAndDuration objects for further analysis.
 */
public class TripsAndDurationDataService extends StatisticsFinlandDataService implements IDataService<TripDataAndDuration> {
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/smat/statfin_smat_pxt_13h2.px";
    private static final String jsonQuery = "{\n" +
    "  \"query\": [\n" +
    "    {\n" +
    "      \"code\": \"Vuosi\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"2021\",\n" +
    "          \"2022\",\n" +
    "          \"2023\",\n" +
    "          \"2024\"\n" +
    "        ]\n" +
    "      }\n" +
    "    },\n" +
    "    {\n" +
    "      \"code\": \"Matkan kohdekunta\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"KU049\",\n" +
    "          \"KU091\",\n" +
    "          \"KU109\",\n" +
    "          \"KU179\",\n" +
    "          \"KU564\",\n" +
    "          \"KU837\",\n" +
    "          \"KU853\",\n" +
    "          \"KU905\"\n" +
    "        ]\n" +
    "      }\n" +
    "    },\n" +
    "    {\n" +
    "      \"code\": \"Tiedot\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"yop_matkoja\",\n" +
    "          \"viipyma\"\n" +
    "        ]\n" +
    "      }\n" +
    "    }\n" +
    "  ],\n" +
    "  \"response\": {\n" +
    "    \"format\": \"json\"\n" +
    "  }\n" +
    "}";

    JsonObject objStatistic = new JsonObject();
    JsonArray statistics = null;

    private static final Map<String, String> destinationMap = Map.of(
        "KU049", "Espoo", 
        "KU091", "Helsinki",
        "KU109", "Hämeenlinna",
        "KU179", "Jyväskylä", 
        "KU564", "Oulu", 
        "KU837", "Tampere",
        "KU853", "Turku", 
        "KU905", "Vaasa"
    );

    public TripsAndDurationDataService() {
        super(API_URL);
        objStatistic= super.fetchData(jsonQuery);  
        statistics = objStatistic.getAsJsonArray("data");   
    }

    @Override
    public List<TripDataAndDuration> processData(){
        List<TripDataAndDuration> tripStatisticsList = new ArrayList<>();

        try {
            for (int i = 0; i < statistics.size(); i++) {
                JsonObject dataObject = statistics.get(i).getAsJsonObject();
                JsonArray keyArray = dataObject.getAsJsonArray("key");

                String year = keyArray.get(0).getAsString();
                String destinationKey = keyArray.get(1).getAsString();
                String destinationName = destinationMap.get(destinationKey);
                JsonArray valuesArray = dataObject.getAsJsonArray("values");
                String overnightTripsStr = valuesArray.get(0).getAsString();
                String averageLengthStr = valuesArray.get(1).getAsString();

                // Handle missing data (e.g., ".")
                int overnightTrips = 0;
                if (!overnightTripsStr.equals(".")) {
                    overnightTrips = Integer.parseInt(overnightTripsStr);
                }

                Double averageLengthOfTrip = null;
                if (!averageLengthStr.equals(".")) {
                    averageLengthOfTrip = Double.parseDouble(averageLengthStr);
                }

                // Add the extracted data to the TripStatistics model
                TripDataAndDuration tripStatistics = new TripDataAndDuration(year, destinationName, averageLengthOfTrip, overnightTrips);
                tripStatisticsList.add(tripStatistics);
            }
        } catch (JsonParseException e) {
            System.err.println("Error while processing JSON data");
        } catch (Exception e) {
            System.err.println("Unexpected error during data processing");
        }

        return tripStatisticsList;
    }

    @Override
    public List<TripDataAndDuration> filterData(List<TripDataAndDuration> tripDataList, String selectedYear) {

        List<TripDataAndDuration> filteredList = new ArrayList<>();
    
        for (TripDataAndDuration tripData : tripDataList) {
            if (tripData.getYear().equalsIgnoreCase(selectedYear)) {
                filteredList.add(tripData); // Add the matching entry to the filtered list
            }
        }

        return filteredList;
    }
}
