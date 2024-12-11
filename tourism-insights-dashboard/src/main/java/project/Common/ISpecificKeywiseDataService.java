package project.Common;

import java.util.List;

/**
 * This interface defines a contract for services that process data
 * specific to a given key and return the result as a list of a generic type.
 *
 * By using generics, this interface supports processing various data types,
 * making it reusable across different implementations. 
 */
public interface ISpecificKeywiseDataService <T> {
    List<T> processData(String key);
}
