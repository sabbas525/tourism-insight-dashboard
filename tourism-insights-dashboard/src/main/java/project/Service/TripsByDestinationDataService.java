package project.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import project.Common.IDataService;
import project.Model.TripStatistics;

/**
 * The TripsByDestinationDataService class is a service that extends the StatisticsFinlandDataService
 * and implements the IDataService interface to retrieve and process data about trips by destination from the StatisticsFinland API.
 * 
 * It interacts with the StatisticsFinland API to fetch data on trips categorized by destination, such as regions or countries 
 * of travel. The service uses a predefined JSON query to retrieve this data and processes the API response to extract relevant statistics 
 * on trip destinations. The extracted data is then mapped into a list of TripStatistics objects for further analysis.
 */
public class TripsByDestinationDataService extends StatisticsFinlandDataService implements IDataService<TripStatistics> {
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/smat/statfin_smat_pxt_13mn.px";
    private static final String jsonQuery = "{\n" +
    "  \"query\": [\n" +
    "    {\n" +
    "      \"code\": \"Matkan kohdemaakunta\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"MK01\",\n" +
    "          \"MK02\",\n" +
    "          \"MK06\",\n" +
    "          \"MK19\"\n" +
    "        ]\n" +
    "      }\n" +
    "    },\n" +
    "    {\n" +
    "      \"code\": \"Matkan tarkoitus\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"1\"\n" +
    "        ]\n" +
    "      }\n" +
    "    },\n" +
    "    {\n" +
    "      \"code\": \"Vuosi\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"2021\"\n" +
    "        ]\n" +
    "      }\n" +
    "    },\n" +
    "    {\n" +
    "      \"code\": \"Kausi\",\n" +
    "      \"selection\": {\n" +
    "        \"filter\": \"item\",\n" +
    "        \"values\": [\n" +
    "          \"1\",\n" +
    "          \"2\",\n" +
    "          \"3\"\n" +
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

    // Mapping of keys to destination names
    private static final Map<String, String> DESTINATION_NAME_MAP = new HashMap<>();

    static {
        DESTINATION_NAME_MAP.put("MK01", "Uusimaa");
        DESTINATION_NAME_MAP.put("MK02", "Southwest Finland");
        DESTINATION_NAME_MAP.put("MK06", "Pirkanmaa");
        DESTINATION_NAME_MAP.put("MK19", "Lapland");
    }

    private static final Map<String, String> destinationMap = Map.of(
        "MK01", "Uusimaa", 
        "MK02", "Southwest Finland", 
        "MK06", "Pirkanmaa",
        "MK19", "Lapland"
    );

    private static final Map<String, String> seasonMap = Map.of(
        "1", "Spring", 
        "2", "Summer", 
        "3", "Autumn",
        "4", "Winter"
    );

    public TripsByDestinationDataService() {
        super(API_URL);
        objStatistic= super.fetchData(jsonQuery);  
        statistics = objStatistic.getAsJsonArray("data");   
    }

    @Override
    public List<TripStatistics> processData(){
         List<TripStatistics> tripDataList = new ArrayList<>();

         try {
            for (int i = 0; i < statistics.size(); i++) {
                JsonObject dataObject = statistics.get(i).getAsJsonObject();
                JsonArray keyArray = dataObject.getAsJsonArray("key");

                String destinationKey = keyArray.get(0).getAsString();
                String destinationName = destinationMap.get(destinationKey);
                String seasonKey = keyArray.get(3).getAsString(); // Extract season
                String season = seasonMap.get(seasonKey);
                String tripCount = dataObject.getAsJsonArray("values").get(0).getAsString();

                tripDataList.add(new TripStatistics(destinationName, season, Double.parseDouble(tripCount)));        
            }
        } catch (JsonParseException e) {
            System.err.println("Error while processing JSON data");
        } catch (Exception e) {
            System.err.println("Unexpected error during data processing");
        }

        return tripDataList;
    }

    @Override
    public List<TripStatistics> filterData(List<TripStatistics> tripDataList, String selectedSeason) {

        List<TripStatistics> filteredList = new ArrayList<>();
    
        for (TripStatistics tripStatistics : tripDataList) {
            // Check if the current season matches the selected season
            if (tripStatistics.getSeason().equalsIgnoreCase(selectedSeason)) {
                filteredList.add(tripStatistics); // Add the matching entry to the filtered list
            }
        }

        return filteredList;
    }
}
