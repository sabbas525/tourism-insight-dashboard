package project.ServiceFacade;

import com.google.gson.JsonObject;
import project.Model.EconomicImpactData;
import project.Service.EconomicImpactService;
import project.Common.PreferenceManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EconomicImpactServiceFacade {
    private EconomicImpactService economicImpactService;
    private PreferenceManager preferenceManager;

    public EconomicImpactServiceFacade() {
        this.economicImpactService = new EconomicImpactService();
        this.preferenceManager = new PreferenceManager();
    }

    public JsonObject fetchInitialData() throws IOException {
        return economicImpactService.fetchInitialData();  // Fetches dropdown data
    }

    public JsonObject fetchDataFromAPI(String jsonQuery) throws IOException {
        return economicImpactService.fetchDataFromAPI(jsonQuery);  // Fetches actual data based on filters
    }

    public void saveUserPreferences(String key, String value) {
        preferenceManager.savePreferences(key, value);  // Save user preferences for dropdowns
    }

    public String getUserPreferences(String key, String defaultValue) {
        return preferenceManager.getPreference(key, defaultValue);  // Get saved user preferences
    }

    // This method processes the API response to extract economic impact data
    public List<EconomicImpactData> processResponseData(JsonObject responseData) {
        return economicImpactService.processData(responseData);  // Return actual data as List
    }

    // This method processes initial dropdown data
    public Map<String, Map<String, String>> processInitialData(JsonObject data) {
        return economicImpactService.processInitialData(data);  // Return dropdown options as Map
    }
}
