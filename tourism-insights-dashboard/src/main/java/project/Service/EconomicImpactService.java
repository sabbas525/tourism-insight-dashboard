package project.Service;

import com.google.gson.*;
import project.Model.EconomicImpactData;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The EconomicImpactService class is responsible for fetching and processing economic impact data
 * related to tourism from an external API provided by VisitFinland.
 * <p>
 * This service handles the interaction with the VisitFinland API by making HTTP requests to fetch
 * the data in JSON format. It supports both GET and POST requests and parses the fetched JSON data
 * for further processing.
 */
public class EconomicImpactService {
    // The API URL for accessing economic impact data
    private static final String API_URL = "https://visitfinland.stat.fi/PXWeb/api/v1/en/VisitFinland/Alueellinen_matkailutilinpito/040_amtp_tau_104.px";

    /**
     * Fetches the initial data from the VisitFinland API.
     * This data is used to populate dropdowns and other UI elements.
     *
     * @return A JsonObject containing the initial data fetched from the API
     * @throws IOException if there is an error during the HTTP request or data reading
     */
    public JsonObject fetchInitialData() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch initial data: HTTP code " + responseCode);
        }

        // Reading the response from the API
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();

        // Parse the response string into a JsonObject
        JsonParser parser = new JsonParser();
        return parser.parse(response.toString()).getAsJsonObject();
    }

    /**
     * Fetches data from the VisitFinland API using a POST request.
     * The method accepts a JSON query string that specifies the filters for the data.
     *
     * @param jsonQuery The JSON query string containing filter parameters
     * @return A JsonObject containing the response data from the API
     * @throws IOException if there is an error during the HTTP request or data reading
     */
    public JsonObject fetchDataFromAPI(String jsonQuery) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Sending the JSON query data in the POST request
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonQuery.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to fetch data: HTTP code " + responseCode);
        }

        // Reading the response from the API
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        conn.disconnect();

        // Parse the response string into a JsonObject
        JsonParser parser = new JsonParser();
        return parser.parse(response.toString()).getAsJsonObject();
    }

    /**
     * Processes the initial data fetched from the API to create a mapping of dropdown values.
     * This method structures the data for easy use in the UI dropdowns.
     *
     * @param data The initial data fetched from the API in JSON format
     * @return A map containing the processed dropdown data (codes -> values)
     */
    public Map<String, Map<String, String>> processInitialData(JsonObject data) {
        Map<String, Map<String, String>> dropdownData = new HashMap<>();

        JsonArray variables = data.getAsJsonArray("variables");
        for (JsonElement variableElement : variables) {
            JsonObject variable = variableElement.getAsJsonObject();
            String code = variable.get("code").getAsString();
            JsonArray values = variable.getAsJsonArray("values");
            JsonArray valueTexts = variable.getAsJsonArray("valueTexts");

            // Create a map of value -> text pairs
            Map<String, String> options = new LinkedHashMap<>();
            for (int i = 0; i < values.size(); i++) {
                options.put(values.get(i).getAsString(), valueTexts.get(i).getAsString());
            }
            dropdownData.put(code, options);
        }
        return dropdownData;
    }

    /**
     * Processes the economic impact data fetched from the API.
     * This method maps the raw data into a list of EconomicImpactData objects.
     *
     * @param responseData The response data fetched from the API in JSON format
     * @return A list of EconomicImpactData objects containing the processed data
     */
    public List<EconomicImpactData> processData(JsonObject responseData) {
        List<EconomicImpactData> dataList = new ArrayList<>();

        JsonArray values = responseData.getAsJsonArray("value");
        JsonObject dimension = responseData.has("dimension") ? responseData.getAsJsonObject("dimension") : null;

        if (dimension != null) {
            // Extracting indices and labels for each dimension (product, region, year, type)
            JsonObject productIndex = dimension.getAsJsonObject("Tuotteet").getAsJsonObject("category").getAsJsonObject("index");
            JsonObject regionIndex = dimension.getAsJsonObject("Maakunta").getAsJsonObject("category").getAsJsonObject("index");
            JsonObject yearIndex = dimension.getAsJsonObject("Vuosi").getAsJsonObject("category").getAsJsonObject("index");
            JsonObject typeIndex = dimension.getAsJsonObject("Matkailutyyppi").getAsJsonObject("category").getAsJsonObject("index");

            JsonObject productLabels = dimension.getAsJsonObject("Tuotteet").getAsJsonObject("category").getAsJsonObject("label");
            JsonObject regionLabels = dimension.getAsJsonObject("Maakunta").getAsJsonObject("category").getAsJsonObject("label");
            JsonObject yearLabels = dimension.getAsJsonObject("Vuosi").getAsJsonObject("category").getAsJsonObject("label");
            JsonObject typeLabels = dimension.getAsJsonObject("Matkailutyyppi").getAsJsonObject("category").getAsJsonObject("label");

            // Sorting keys by index to maintain proper order
            List<String> productKeys = sortKeysByIndex(productIndex);
            List<String> regionKeys = sortKeysByIndex(regionIndex);
            List<String> yearKeys = sortKeysByIndex(yearIndex);
            List<String> typeKeys = sortKeysByIndex(typeIndex);

            int index = 0;
            // Loop through all combinations of product, region, year, and type
            for (String productKey : productKeys) {
                String productLabel = productLabels.get(productKey).getAsString();
                for (String regionKey : regionKeys) {
                    String regionLabel = regionLabels.get(regionKey).getAsString();
                    for (String yearKey : yearKeys) {
                        String yearLabel = yearLabels.get(yearKey).getAsString();
                        for (String typeKey : typeKeys) {
                            String typeLabel = typeLabels.get(typeKey).getAsString();

                            // Get the economic impact value for the current combination of filters
                            double value = 0.0;
                            if (index < values.size() && !values.get(index).isJsonNull()) {
                                value = values.get(index).getAsDouble();
                            }

                            // Create an EconomicImpactData object and add it to the list
                            EconomicImpactData dataModel = new EconomicImpactData(productLabel, regionLabel, yearLabel, typeLabel, value);
                            dataList.add(dataModel);
                            index++;
                        }
                    }
                }
            }
        } else {
            // Handle case where "dimension" is missing or null
            System.out.println("Dimension data is missing in the response.");
        }

        return dataList;
    }

    /**
     * Helper method to sort the keys based on their corresponding index value.
     * This ensures the correct order when processing the data.
     *
     * @param indexObject The JSON object containing the index values
     * @return A list of sorted keys
     */
    private List<String> sortKeysByIndex(JsonObject indexObject) {
        List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(indexObject.entrySet());
        // Sorting the entries by the index value (ascending)
        entries.sort(Comparator.comparingInt(e -> e.getValue().getAsInt()));

        List<String> sortedKeys = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : entries) {
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }
}
