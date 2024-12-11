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
 * and implements the IDataService interface to retrieve and process foreign visitor data from the StatisticsFinland API.
 * 
 * This service interacts with the StatisticsFinland API to fetch data on foreign visitors, categorized by destination
 * and other criteria. It uses a predefined JSON query to fetch the data, then processes the API response to extract
 * relevant tourist data and maps it into a list of TouristData objects.
 */
public class ForeignVisitorsDataService extends StatisticsFinlandDataService implements IDataService<TouristData> {
  
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin_Passiivi/smat/statfinpas_smat_pxt_133i_2021.px";
    private static String jsonQuery = "{\r\n" + //
                "  \"query\": [\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Majoitus\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"0\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Vuosi\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"2021\"\r\n" + //
                "        ]\r\n" + //
                "      }\r\n" + //
                "    },\r\n" + //
                "    {\r\n" + //
                "      \"code\": \"Tiedot\",\r\n" + //
                "      \"selection\": {\r\n" + //
                "        \"filter\": \"item\",\r\n" + //
                "        \"values\": [\r\n" + //
                "          \"yot\"\r\n" + //
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

    public ForeignVisitorsDataService() {
        super(API_URL);
        objVisitors = super.fetchData(jsonQuery); 
    }

    @Override
    public List<TouristData> processData()
    {
        List<TouristData> touristCountDataList = new ArrayList<>();

        if(objVisitors != null)
        {
            JsonObject dimension = objVisitors.getAsJsonObject("dimension");
            JsonObject matkanKohdekunta = dimension.getAsJsonObject("Matkan kohdekunta");
            JsonObject category = matkanKohdekunta.getAsJsonObject("category");
            JsonObject index = category.getAsJsonObject("index");
            JsonObject label = category.getAsJsonObject("label");
            JsonArray values = objVisitors.getAsJsonArray("value");
            
            for (Map.Entry<String, JsonElement> entry : index.entrySet()) {
                String regionCode = entry.getKey();
                int indexValue = entry.getValue().getAsInt();
                String regionName = label.get(regionCode).getAsString();
                int touristno;
                if (values.get(indexValue) != null && !values.get(indexValue).isJsonNull()) {
                    touristno = values.get(indexValue).getAsInt();
                } else {
                    touristno = 0;
                }
                touristCountDataList.add(new TouristData(regionCode, regionName, touristno));
            }
        }
        return touristCountDataList;
    }

    @Override
    public List<TouristData> filterData(List<TouristData> touristDataList, String selectedRegion) 
    {

        List<TouristData> filteredList = new ArrayList<>();
        String wholeCountryCode = "SSS";
        for (TouristData touristData : touristDataList) {
            if (touristData.getRegionID().equalsIgnoreCase(wholeCountryCode)) {
                filteredList.add(touristData); 
            }
        }

        if(selectedRegion != null && selectedRegion != "Finland")
        {
            for (TouristData touristData : touristDataList) {
                if (touristData.getRegionName().equalsIgnoreCase(selectedRegion)) {
                    filteredList.add(touristData); 
                }
            }
         }

        return filteredList;
    }
    
}
