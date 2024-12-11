package project.Common;

import com.google.gson.JsonObject;

/**
 * The DataRetrievable interface defines a contract for fetching data 
 * from an external source using a specified JSON query. It serves as 
 * an abstraction for data retrieval operations
 */
public interface DataRetrievable {
    JsonObject fetchData(String jsonQuery);
}