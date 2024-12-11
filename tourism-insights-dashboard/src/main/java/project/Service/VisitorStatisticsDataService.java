package project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import project.Common.IDataService;
import project.Model.VisitorStatistics;

/**
 * The VisitorStatisticsDataService class is a service that extends the StatisticsFinlandDataService
 * and implements the IDataService interface to retrieve and process visitor statistics data from the StatisticsFinland API.
 * 
 * This service interacts with the StatisticsFinland API to fetch data on visitor statistics, such as the number of visitors
 * per year, categorized by various factors like age group, gender, or purpose of visit. It uses a predefined JSON query to request
 * the data, then processes the response to extract relevant statistics. The extracted data is mapped into a list of 
 * VisitorStatistics objects for further analysis or display in the application.
 */
public class VisitorStatisticsDataService extends StatisticsFinlandDataService implements IDataService<VisitorStatistics>{
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin/smat/statfin_smat_pxt_13yi.px";
    private static final String jsonQuery = "{"
    + "\"query\": ["
    + "{"
    + "\"code\": \"Vuosi\","
    + "\"selection\": {"
    + "\"filter\": \"item\","
    + "\"values\": [\"2023\"]"
    + "}"
    + "},"
    + "{"
    + "\"code\": \"Ikäryhmä\","
    + "\"selection\": {"
    + "\"filter\": \"item\","
    + "\"values\": [\"1\", \"2\", \"3\"]"
    + "}"
    + "},"
    + "{"
    + "\"code\": \"Sukupuoli\","
    + "\"selection\": {"
    + "\"filter\": \"item\","
    + "\"values\": [\"1\", \"2\"]"
    + "}"
    + "},"
    + "{"
    + "\"code\": \"Tiedot\","
    + "\"selection\": {"
    + "\"filter\": \"item\","
    + "\"values\": [\"ulkvap_os\", \"rist_os\", \"paivam_os\", \"yopvap_os\", \"kotvap_os\"]"
    + "}"
    + "}"
    + "],"
    + "\"response\": {"
    + "\"format\": \"json\""
    + "}"
    + "}";

    JsonObject objStatistic = new JsonObject();
    JsonArray statistics = null;

    // Maps for readable labels
    private static final Map<String, String> ageGroupMap = Map.of(
        "1", "15 - 24", 
        "2", "25 - 64", 
        "3", "65 - 84"
    );

    private static final Map<String, String> genderMap = Map.of(
        "1", "Male", 
        "2", "Female"
    );
    
    // Define trip types in the same order as the "values" array
    private static final String[] tripTypes = {
        "Leisure Trip Abroad", 
        "Cruise", 
        "Same-Day Visit Abroad", 
        "Domestic Leisure Trip", 
        "Domestic Visit"
    };

    public VisitorStatisticsDataService() {
        super(API_URL);
        objStatistic= super.fetchData(jsonQuery);  
        statistics = objStatistic.getAsJsonArray("data");  
    }

    @Override
    public List<VisitorStatistics> processData(){
        List<VisitorStatistics> visitorDataList = new ArrayList<>();

        try {
            // Loop through the data array
            for (int i = 0; i < statistics.size(); i++) {
                JsonObject dataObject = statistics.get(i).getAsJsonObject();
                JsonArray keyArray = dataObject.getAsJsonArray("key");
                JsonArray valuesArray = dataObject.getAsJsonArray("values");
    
                // Extract values from the key array
                String year = keyArray.get(0).getAsString();
                String ageGroupCode = keyArray.get(1).getAsString();
                String genderCode = keyArray.get(2).getAsString();
    
                // Map age group and gender to readable values
                String ageGroup = ageGroupMap.getOrDefault(ageGroupCode, "Unknown age group");
                String gender = genderMap.getOrDefault(genderCode, "Unknown gender");
    
                // Loop through each trip type and corresponding percentage
                for (int j = 0; j < tripTypes.length; j++) {
                    String tripType = tripTypes[j];
                    double percentage = Double.parseDouble(valuesArray.get(j).getAsString());
    
                    // Create a new VisitorStatistics object for each trip type
                    VisitorStatistics visitorStatistics = new VisitorStatistics(
                        year, 
                        ageGroup, 
                        gender, 
                        tripType, 
                        percentage
                    );
    
                    // Add the object to the list
                    visitorDataList.add(visitorStatistics);
                }
            }
        } catch (JsonParseException e) {
            System.err.println("Error while processing JSON data");
        } catch (Exception e) {
            System.err.println("Unexpected error during data processing");
        }

    return visitorDataList;
}

    @Override
    public List<VisitorStatistics> filterData(List<VisitorStatistics> visitorDataList, String tripType) {
        List<VisitorStatistics> filteredList = new ArrayList<>();
    
        for (VisitorStatistics visitorStatistics : visitorDataList) {
            // Check if the trip type matches the desired tripType
            if (visitorStatistics.getTripType().equalsIgnoreCase(tripType)) {
                filteredList.add(visitorStatistics); 
            }
        }
    
        return filteredList;
    }

    public Map<String, Double> GetAgeGroupCounts(List<VisitorStatistics> visitorDataList, String tripType){
        // Create a map to hold total percentages for each trip type
        Map<String, Double> ageCounts = new HashMap<>();

        // Filter and sum percentages by trip type for the selected age group
        for (VisitorStatistics stats : visitorDataList) {
            if (stats.getTripType().equals(tripType)) {
                String ageGroup = stats.getAgeGroup();
                double percentage = stats.getPercentage();

                // Accumulate the percentage for each trip type
                ageCounts.put(ageGroup, ageCounts.getOrDefault(ageGroup, 0.0) + percentage);
            }
        }

        return ageCounts;
    }
}
