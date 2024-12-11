package project.Common;

import java.util.List;

/**
 * The IDataService interface defines a generic contract for processing and filtering data.
 * It is designed to support with various types of data by leveraging  generics, providing 
 * a flexible and reusable framework for data handling operations.
 */
public interface IDataService<T> {
    List<T> processData();
    List<T> filterData(List<T> tripDataList, String filter);
}
