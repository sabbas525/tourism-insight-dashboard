package project;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.TestInstance;
import project.Model.EconomicImpactData;
import project.ServiceFacade.EconomicImpactServiceFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EconomicImpactServiceFacadeTest {

    private EconomicImpactServiceFacade serviceFacade;

    @BeforeEach
    public void setUp() {
        serviceFacade = new EconomicImpactServiceFacade(); // Instantiate service facade
    }

    @Test
    void testFetchDataFromAPI() {
        String query = "";  // Replace with a valid query string
        try {
            JsonObject response = serviceFacade.fetchDataFromAPI(query);
            assertNotNull(response);
        } catch (IOException e) {
            System.err.println("Failed to fetch data: " + e.getMessage());
            fail("API call failed");
        }
    }


    @Test
    void testProcessResponseData() {
        // Test the data processing logic after an API call

        // Create a mock JsonObject response
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("data", "mockResponseData");  // Adding a mock data field

        // Ensure the processResponseData method works as expected with a mock response
        assertNotNull(serviceFacade.processResponseData(jsonResponse));
    }

    @Test
    void testSaveUserPreferences() {
        // Test saving preferences to ensure that the facade's save functionality works
        serviceFacade.saveUserPreferences("product", "Product1");
        assertEquals("Product1", serviceFacade.getUserPreferences("product", ""));
    }

    @Test
    void testGetUserPreferences() {
        // Test that saved preferences can be retrieved correctly
        serviceFacade.saveUserPreferences("region", "Region1");
        assertEquals("Region1", serviceFacade.getUserPreferences("region", ""));
    }
    
}
