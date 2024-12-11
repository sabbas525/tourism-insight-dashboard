package project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import project.Common.IDataService;
import project.Model.*;

/**
 * This class is a service that extends the StatisticsFinlandDataService
 * and implements the IDataService interface to process and retrieve tourist data by trip purpose from the VisitFinland API.
 * 
 * This service fetches and processes data related to foreign visitors categorized by the purpose of their visit 
 * (e.g., leisure, business, etc.). It uses a predefined JSON query to interact with the VisitFinland API, 
 * parsing the response to extract relevant tourist data by purpose.
 */
public class ForeignVisitorsByPurposeDataService extends StatisticsFinlandDataService implements IDataService<TouristDatabyPurpose> {
  
    private static final String API_URL = "https://visitfinland.stat.fi:443/PXWeb/api/v1/fi/VisitFinland/Matkailijamittari/visitfinland_ulma_pxt_14tj.px";
    private static String jsonQuery = "{\r\n" + //
                "  \"query\": [\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Matkan tarkoitus\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"1\",\r\n" + //
                "          \"2\",\r\n" + //
                "          \"3\",\r\n" + //
                "          \"4\",\r\n" + //
                "          \"5\",\r\n" + //
                "          \"6\",\r\n" + //
                "          \"7\",\r\n" + //
                "          \"8\",\r\n" + //
                "          \"9\",\r\n" + //
                "          \"10\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Matkan kesto\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"0\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Kulkuväline\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"0\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Matkustajan asuinmaa\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"SSS\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Tiedot\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"matkoja_r12\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    }\r\n" + //
                "  ],\r\n" + //
                "  \"response\": {\r\n" + //
                "    \"format\": \"json-stat2\"\r\n" + //
                "  }\r\n" + //
                "}";

    JsonObject objVisitors = new JsonObject();
    JsonArray visitorsData = null;
    
    
    private static final Map<String, String> purposeMap = Map.of(
        "1", "Personal",
        "2", "Holiday",
        "3", "Visit",
        "4", "Studying",
        "5", "Other personal reasons",
        "6", "Work",
        "7", "Business trip",
        "8", "Conference or fair",
        "9", "Finnish employer",
        "10", "Other work reasons"
    );

    public ForeignVisitorsByPurposeDataService() {
        super(API_URL);
        objVisitors = super.fetchData(jsonQuery); 
    }

    @Override
    public List<TouristDatabyPurpose> processData()
    {
        List<TouristDatabyPurpose> touristDatabyPurposeList = new ArrayList<>();
        if(objVisitors != null)
        {
            JsonObject dimension = objVisitors.getAsJsonObject("dimension");
            JsonObject matkantarkoitus = dimension.getAsJsonObject("Matkan tarkoitus");
            JsonObject category = matkantarkoitus.getAsJsonObject("category");
            JsonObject index = category.getAsJsonObject("index");
            JsonObject label = category.getAsJsonObject("label");
            JsonArray values = objVisitors.getAsJsonArray("value");
            
            for (Map.Entry<String, JsonElement> entry : index.entrySet()) {
                String purposeId = entry.getKey();
                int indexValue = entry.getValue().getAsInt();
                String purpose = purposeMap.get(purposeId);
                Double touristno;
                if (values.get(indexValue) != null && !values.get(indexValue).isJsonNull()) {
                    touristno = values.get(indexValue).getAsDouble();
                } else {
                    touristno = 0.0;
                }
                touristDatabyPurposeList.add(new TouristDatabyPurpose(purposeId, purpose, touristno));
            }
        }
        return touristDatabyPurposeList;
    }

    @Override
    public List<TouristDatabyPurpose> filterData(List<TouristDatabyPurpose> touristDatabyPurposeList, String filterStr) 
    {
        List<TouristDatabyPurpose> filteredList = new ArrayList<>();
        
        var purposeIDs = filterStr.split(",");
        for (String purposeID : purposeIDs) {
            for (TouristDatabyPurpose touristDatabyPurpose : touristDatabyPurposeList) {
                if (touristDatabyPurpose.getPurposeID().equalsIgnoreCase(purposeID)) {
                    filteredList.add(touristDatabyPurpose); 
                }
            }
        }
        return filteredList;
    }
    
}
