package project.Service;

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ContentType;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import project.Common.DataRetrievable;

/**
 * The StatisticsFinlandDataService class is an abstract base service for interacting with the StatisticsFinland API.
 * It implements the DataRetrievable interface to define methods for fetching and processing data from the API.
 * 
 * It provides functionality for sending POST requests with a JSON payload to the StatisticsFinland API. 
 * The response is then parsed into a JsonObject. It also contains common logic for interacting with the API that 
 * can be extended by concrete subclasses to implement specific data processing tasks.
 */
public  abstract class StatisticsFinlandDataService implements DataRetrievable{
    String apiUrl = "";
    String jsonQuery = "";

    public StatisticsFinlandDataService(String apiUrl){
        this.apiUrl = apiUrl;
    }

    @Override
    public JsonObject fetchData(String jsonQuery) {
        JsonObject apiResult = null;
       
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity requestEntity = new StringEntity(jsonQuery, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            // Execute the request and process the response
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();
                System.out.println("Response Status: " + statusCode);

                if (statusCode == 200) {
                    // Get the response entity
                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);

                    //parse the response using Gson
                    apiResult = parseData(responseBody);
                } else {
                    System.out.println("POST request failed. Response Code: " + statusCode);
                }
            }
            catch(Exception exception){
                exception.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiResult != null ? apiResult : null;
    }

    // Method to parse the JSON response into objects
    private JsonObject parseData(String responseBody) {
        Gson gson = new Gson();
        JsonObject jsonData = gson.fromJson(responseBody, JsonObject.class);
        return jsonData;
    }
}
