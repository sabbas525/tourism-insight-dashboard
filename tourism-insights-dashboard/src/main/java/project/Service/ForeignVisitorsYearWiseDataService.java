package project.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import project.Common.ISpecificKeywiseDataService;
import project.Model.*;

/**
 * This class is a service that extends the StatisticsFinlandDataService
 * and implements the ISpecificKeywiseDataService interface to retrieve and process year-wise foreign visitor data 
 * from the StatisticsFinland API.
 * 
 * This service interacts with the StatisticsFinland API to fetch year-wise foreign visitor data. It uses a predefined
 * JSON query to retrieve the data and processes the API response to extract the relevant statistics on foreign visitors
 * for each year. The processed data is then mapped into a list of TouristYearData objects.
 */
public class ForeignVisitorsYearWiseDataService extends StatisticsFinlandDataService implements ISpecificKeywiseDataService<TouristYearData>{
  
    private static final String API_URL = "https://pxdata.stat.fi:443/PxWeb/api/v1/en/StatFin_Passiivi/smat/statfinpas_smat_pxt_133i_2021.px";
    private static String jsonQuery = "{\r\n" + //
                "    \"query\": [\r\n" + //
                "        {\r\n" + //
                "            \"code\": \"Majoitus\",\r\n" + //
                "            \"selection\": {\r\n" + //
                "                \"filter\": \"item\",\r\n" + //
                "                \"values\": [\r\n" + //
                "                    \"0\"\r\n" + //
                "                ]\r\n" + //
                "            }\r\n" + //
                "        },\r\n" + //
                "        {\r\n" + //
                "            \"code\": \"Matkan kohdekunta\",\r\n" + //
                "            \"selection\": {\r\n" + //
                "                \"filter\": \"item\",\r\n" + //
                "                \"values\": [\r\n" + //
                "                    _locationsCode_\r\n" + //
                "                ]\r\n" + //
                "            }\r\n" + //
                "        },\r\n" + //
                "        {\r\n" + //
                "            \"code\": \"Tiedot\",\r\n" + //
                "            \"selection\": {\r\n" + //
                "                \"filter\": \"item\",\r\n" + //
                "                \"values\": [\r\n" + //
                "                    \"yot\"\r\n" + //
                "                ]\r\n" + //
                "            }\r\n" + //
                "        }\r\n" + //
                "    ],\r\n" + //
                "    \"response\": {\r\n" + //
                "        \"format\": \"json-stat2\"\r\n" + //
                "    }\r\n" + //
                "}";

    JsonObject objVisitors = new JsonObject();
    JsonArray visitorsData = null;

    public ForeignVisitorsYearWiseDataService() {
        super(API_URL);
    }

    @Override
    public List<TouristYearData> processData(String LocationCode)
    {
        List<TouristYearData> touristYearDataList = new ArrayList<>();

        try
        {
            if(LocationCode == null || LocationCode == "" || LocationCode == "Finland")
            {
                jsonQuery = jsonQuery.replace("_locationsCode_", "\"SSS\"");
            }
            else
            {
                jsonQuery = jsonQuery.replace("_locationsCode_", "\"" + LocationCode + "\"");
            }
            objVisitors = super.fetchData(jsonQuery); 
            if(objVisitors != null)
            {
                JsonObject dimension = objVisitors.getAsJsonObject("dimension");
                JsonObject vuosi = dimension.getAsJsonObject("Vuosi");
                JsonObject category = vuosi.getAsJsonObject("category");
                JsonObject index = category.getAsJsonObject("index");
                JsonObject label = category.getAsJsonObject("label");
                JsonArray values = objVisitors.getAsJsonArray("value");
                
                for (Map.Entry<String, JsonElement> entry : index.entrySet()) {
                    String year = entry.getKey();
                    int indexValue = entry.getValue().getAsInt();
                    String yearTxt = label.get(year).getAsString();
                    Double touristno;
                    if (values.get(indexValue) != null && !values.get(indexValue).isJsonNull()) {
                        touristno = values.get(indexValue).getAsDouble();
                    } else {
                        touristno = 0.0;
                    }
                    touristYearDataList.add(new TouristYearData(yearTxt,  touristno));
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return touristYearDataList;
    }

    
}
