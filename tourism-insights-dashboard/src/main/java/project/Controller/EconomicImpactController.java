package project.Controller;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.Main;
import project.Model.EconomicImpactData;
import project.ServiceFacade.EconomicImpactServiceFacade;
import org.controlsfx.control.CheckComboBox;
import com.google.gson.*;

import java.io.IOException;
import java.util.*;

/**
 * The EconomicImpactController handles the user interface logic for displaying economic impact data.
 * It manages the filters (product, region, year, type) and updates the UI with relevant data.
 * It interacts with the EconomicImpactServiceFacade to fetch and process data from an external source.
 */
public class EconomicImpactController {

    // FXML fields for the user interface components
    @FXML
    private CheckComboBox<String> productChoiceBox;
    @FXML
    private CheckComboBox<String> regionChoiceBox;
    @FXML
    private CheckComboBox<String> yearChoiceBox;
    @FXML
    private CheckComboBox<String> typeChoiceBox;
    @FXML
    private TableView<EconomicImpactData> dataTableView;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private PieChart pieChart;

    // Service facade to interact with backend data
    private EconomicImpactServiceFacade facade;

    // Data for populating dropdowns
    public Map<String, Map<String, String>> dropdownData;

    /**
     * Initializes the controller by fetching the initial data and populating dropdowns.
     */
    @FXML
    public void initialize() {
        facade = new EconomicImpactServiceFacade(); // Initialize the service facade

        try {
            // Fetch initial data from the service
            JsonObject initialData = facade.fetchInitialData();
            dropdownData = facade.processInitialData(initialData); // Process dropdown data
            populateDropdowns(); // Populate the dropdown menus
            loadPreferences(); // Load saved preferences (if any)
        } catch (IOException e) {
            e.printStackTrace(); // Handle potential IO exceptions
        }
    }

    // Navigation methods for switching between pages
    @FXML
    public void switchToHomePage() throws IOException {
        Main.setRoot("/Views/HomePage.fxml");
    }

    @FXML
    public void switchToWeather() throws IOException {
        Main.setRoot("/Views/Weather.fxml");
    }

    @FXML
    public void switchToStatistics() throws IOException {
        Main.setRoot("/Views/Statistics.fxml");
    }

    /**
     * Handles the logout process by closing the current stage.
     */
    @FXML
    public void LogOutProcess(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); // Close the current window
    }

    /**
     * Populates the dropdown menus with data fetched from the backend.
     */
    public void populateDropdowns() {
        // Extract data for each dropdown menu
        Map<String, String> products = dropdownData.get("Tuotteet");
        Map<String, String> regions = dropdownData.get("Maakunta");
        Map<String, String> years = dropdownData.get("Vuosi");
        Map<String, String> types = dropdownData.get("Matkailutyyppi");

        // Convert maps to lists for populating the dropdowns
        List<String> productList = new ArrayList<>(products.values());
        List<String> regionList = new ArrayList<>(regions.values());
        List<String> yearList = new ArrayList<>(years.values());
        List<String> typeList = new ArrayList<>(types.values());

        // Populate the CheckComboBox items
        productChoiceBox.getItems().setAll(productList);
        regionChoiceBox.getItems().setAll(regionList);
        yearChoiceBox.getItems().setAll(yearList);
        typeChoiceBox.getItems().setAll(typeList);

        // Check the first item in each list if it is not empty
        if (!productList.isEmpty()) productChoiceBox.getCheckModel().check(0);
        if (!regionList.isEmpty()) regionChoiceBox.getCheckModel().check(0);
        if (!yearList.isEmpty()) yearChoiceBox.getCheckModel().check(0);
        if (!typeList.isEmpty()) typeChoiceBox.getCheckModel().check(0);
    }

    /**
     * Fetches the data based on selected filters and updates the UI.
     */
    @FXML
    public void fetchData(ActionEvent event) {
        // Get the selected items from the CheckComboBoxes
        List<String> selectedProducts = new ArrayList<>(productChoiceBox.getCheckModel().getCheckedItems());
        List<String> selectedRegions = new ArrayList<>(regionChoiceBox.getCheckModel().getCheckedItems());
        List<String> selectedYears = new ArrayList<>(yearChoiceBox.getCheckModel().getCheckedItems());
        List<String> selectedTypes = new ArrayList<>(typeChoiceBox.getCheckModel().getCheckedItems());

        // Map selected items to their corresponding codes
        List<String> selectedProductCodes = getSelectedCodes("Tuotteet", selectedProducts);
        List<String> selectedRegionCodes = getSelectedCodes("Maakunta", selectedRegions);
        List<String> selectedYearCodes = getSelectedCodes("Vuosi", selectedYears);
        List<String> selectedTypeCodes = getSelectedCodes("Matkailutyyppi", selectedTypes);

        // Build the query JSON based on selected codes
        String jsonQuery = buildQuery(selectedProductCodes, selectedRegionCodes, selectedYearCodes, selectedTypeCodes);

        try {
            // Fetch data from the API based on the constructed query
            JsonObject responseData = facade.fetchDataFromAPI(jsonQuery);
            List<EconomicImpactData> dataModels = facade.processResponseData(responseData); // Process the fetched data
            updateUI(dataModels); // Update the UI with the fetched data
            savePreferences(selectedProducts, selectedRegions, selectedYears, selectedTypes); // Save user preferences
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exceptions
        }
    }

    /**
     * Retrieves the codes corresponding to the selected values from the dropdown.
     */
    public List<String> getSelectedCodes(String variableCode, List<String> displayValues) {
        Map<String, String> options = dropdownData.get(variableCode);
        List<String> selectedCodes = new ArrayList<>();

        // Find the corresponding code for each selected display value
        for (String displayValue : displayValues) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                if (entry.getValue().equals(displayValue)) {
                    selectedCodes.add(entry.getKey());
                    break;
                }
            }
        }
        return selectedCodes;
    }

    /**
     * Constructs the query string in JSON format based on the selected filters.
     */
    public String buildQuery(List<String> products, List<String> regions, List<String> years, List<String> types) {
        JsonArray queryArray = new JsonArray();
        queryArray.add(buildQueryItem("Tuotteet", products)); // Add product filter
        queryArray.add(buildQueryItem("Maakunta", regions)); // Add region filter
        queryArray.add(buildQueryItem("Vuosi", years)); // Add year filter
        queryArray.add(buildQueryItem("Matkailutyyppi", types)); // Add type filter

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("format", "json-stat2");

        JsonObject queryObject = new JsonObject();
        queryObject.add("query", queryArray);
        queryObject.add("response", responseObject);

        return queryObject.toString(); // Return the query string
    }

    /**
     * Builds a query item for a specific filter, such as products, regions, etc.
     */
    public JsonObject buildQueryItem(String code, List<String> values) {
        JsonObject item = new JsonObject();
        item.addProperty("code", code);

        JsonObject selection = new JsonObject();
        selection.addProperty("filter", "item");

        // Add the selected values to the query item
        JsonArray valuesArray = new JsonArray();
        for (String value : values) {
            valuesArray.add(value);
        }
        selection.add("values", valuesArray);

        item.add("selection", selection);

        return item; // Return the query item
    }

    /**
     * Updates the user interface with the fetched data (e.g., populating the table and charts).
     *
     * @param dataModels List of EconomicImpactData to display in the UI
     */
    public void updateUI(List<EconomicImpactData> dataModels) {
        ObservableList<EconomicImpactData> data = FXCollections.observableArrayList(dataModels);
        dataTableView.setItems(data);

        if (dataTableView.getColumns().isEmpty()) {
            TableColumn<EconomicImpactData, String> productCol = new TableColumn<>("Product");
            productCol.setCellValueFactory(cellData -> cellData.getValue().productProperty());

            TableColumn<EconomicImpactData, String> regionCol = new TableColumn<>("Region");
            regionCol.setCellValueFactory(cellData -> cellData.getValue().regionProperty());

            TableColumn<EconomicImpactData, String> yearCol = new TableColumn<>("Year");
            yearCol.setCellValueFactory(cellData -> cellData.getValue().yearProperty());

            TableColumn<EconomicImpactData, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());

            TableColumn<EconomicImpactData, Double> valueCol = new TableColumn<>("Value");
            valueCol.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());

            dataTableView.getColumns().addAll(productCol, regionCol, yearCol, typeCol, valueCol);
        }

        lineChart.getData().clear();
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Value over Years");
        for (EconomicImpactData dm : dataModels) {
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dm.getYear(), dm.getValue());
            lineSeries.getData().add(dataPoint);

            dataPoint.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip tooltip = new Tooltip("Year: " + dm.getYear() + "\nValue: " + dm.getValue());
                    Tooltip.install(newValue, tooltip);
                    Label label = new Label(String.format("%.2f", dm.getValue()));
                    label.setStyle("-fx-font-size: 10px; -fx-text-fill: black;");
                    StackPane.setAlignment(label, Pos.TOP_CENTER);
                    ((StackPane) newValue).getChildren().add(label);
                }
            });
        }
        lineChart.getData().add(lineSeries);

        pieChart.getData().clear();
        Map<String, Double> categoryValues = new HashMap<>();
        for (EconomicImpactData dm : dataModels) {
            String category = dm.getProduct();
            double value = dm.getValue();
            categoryValues.merge(category, value, Double::sum);
        }
        if (categoryValues.isEmpty()) {
            PieChart.Data slice = new PieChart.Data("No Data", 1);
            pieChart.getData().add(slice);
        } else {
            for (Map.Entry<String, Double> entry : categoryValues.entrySet()) {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
            }
        }
    }

    /**
     * Saves the selected filter preferences for future use.
     * This method saves the user's selected filters (products, regions, years, types) into storage (file/database).
     *
     * @param selectedProducts List of selected product filters
     * @param selectedRegions  List of selected region filters
     * @param selectedYears    List of selected year filters
     * @param selectedTypes    List of selected type filters
     */
    public void savePreferences(List<String> selectedProducts, List<String> selectedRegions, List<String> selectedYears, List<String> selectedTypes) {
        facade.saveUserPreferences("products", String.join(",", selectedProducts));
        facade.saveUserPreferences("regions", String.join(",", selectedRegions));
        facade.saveUserPreferences("years", String.join(",", selectedYears));
        facade.saveUserPreferences("types", String.join(",", selectedTypes));
    }

    /**
     * Loads the user preferences for filters (if any).
     * This method retrieves the last used filter selections (if saved) and applies them to the CheckComboBox filters.
     */
    public void loadPreferences() {
        String savedProducts = facade.getUserPreferences("products", "");
        String savedRegions = facade.getUserPreferences("regions", "");
        String savedYears = facade.getUserPreferences("years", "");
        String savedTypes = facade.getUserPreferences("types", "");

        // Convert saved preferences to indices and check them
        checkSelectedItems(productChoiceBox, savedProducts);
        checkSelectedItems(regionChoiceBox, savedRegions);
        checkSelectedItems(yearChoiceBox, savedYears);
        checkSelectedItems(typeChoiceBox, savedTypes);
    }

    public void checkSelectedItems(CheckComboBox<String> checkComboBox, String savedItems) {
        if (!savedItems.isEmpty()) {
            String[] selectedItems = savedItems.split(",");
            for (String item : selectedItems) {
                int index = checkComboBox.getItems().indexOf(item);
                if (index != -1) {
                    checkComboBox.getCheckModel().check(index);  // Check the item at the specific index
                }
            }
        }
    }
}
