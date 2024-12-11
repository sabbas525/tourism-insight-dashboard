package project.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project.Main;
import project.Model.TrafficData;
import project.Model.WeatherData;
import project.Service.TrafficService;
import project.Service.WeatherService;
import project.Common.PreferenceManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Controller is responsible for managing the interactions between the weather and traffic services in the application.
 * It initializes these services and provides methods to fetch and display relevant data (weather conditions and traffic statistics).
 */
public class WeatherController {

    // Weather Filters
    @FXML
    private ListView<Integer> weatherStationFilter;
    @FXML
    private DatePicker weatherFromDatePicker;
    @FXML
    private DatePicker weatherToDatePicker;

    // Traffic Filters
    @FXML
    private ListView<Integer> trafficStationFilter;
    @FXML
    private DatePicker trafficFromDatePicker;
    @FXML
    private DatePicker trafficToDatePicker;

    // Weather UI Components
    @FXML
    private PieChart weatherPieChart;
    @FXML
    private TableView<WeatherData> weatherTable;
    @FXML
    private TableColumn<WeatherData, Integer> stationIdColumn;
    @FXML
    private TableColumn<WeatherData, String> dateColumn;
    @FXML
    private TableColumn<WeatherData, Double> temperatureColumn;
    @FXML
    private TableColumn<WeatherData, Double> precipitationColumn;
    @FXML
    private TableColumn<WeatherData, Double> windSpeedColumn;

    // Traffic UI Components
    @FXML
    private PieChart trafficPieChart;
    @FXML
    private TableView<TrafficData> trafficTable;
    @FXML
    private TableColumn<TrafficData, Integer> trafficStationIdColumn;
    @FXML
    private TableColumn<TrafficData, String> trafficDateColumn;
    @FXML
    private TableColumn<TrafficData, Double> volumeColumn;
    @FXML
    private TableColumn<TrafficData, Double> speedColumn;

    // Combined Line Chart
    @FXML
    private LineChart<String, Number> combinedLineChart;

    private WeatherService weatherService;
    private TrafficService trafficService;

    @FXML
    public void initialize() {
        weatherService = new WeatherService();
        trafficService = new TrafficService();

        // Set default dates for weather and traffic
        LocalDate defaultFromDate = LocalDate.now().minusDays(1);
        LocalDate defaultToDate = LocalDate.now();

        weatherFromDatePicker.setValue(defaultFromDate);
        weatherToDatePicker.setValue(defaultToDate);
        trafficFromDatePicker.setValue(defaultFromDate);
        trafficToDatePicker.setValue(defaultToDate);

        // Initialize Weather Table Columns
        stationIdColumn.setCellValueFactory(new PropertyValueFactory<>("stationId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("measurementTime"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("airTemperature"));
        precipitationColumn.setCellValueFactory(new PropertyValueFactory<>("precipitation"));
        windSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("windSpeed"));

        // Initialize Traffic Table Columns
        trafficStationIdColumn.setCellValueFactory(new PropertyValueFactory<>("stationId"));
        trafficDateColumn.setCellValueFactory(new PropertyValueFactory<>("measurementTime"));
        volumeColumn.setCellValueFactory(new PropertyValueFactory<>("volume"));
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("speed"));

        // Initialize Filters with separate station IDs for weather and traffic
        weatherStationFilter.setItems(FXCollections.observableArrayList(getAvailableWeatherStationIds()));
        trafficStationFilter.setItems(FXCollections.observableArrayList(getAvailableTrafficStationIds()));

        // Load preferences if they exist
        loadPreferences();

        // Select all stations by default
        weatherStationFilter.getSelectionModel().selectAll();
        trafficStationFilter.getSelectionModel().selectAll();

        // Set multiple selection mode
        weatherStationFilter.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        trafficStationFilter.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Load initial data
        updateData();

        // Add listeners to update data when filters change
        weatherFromDatePicker.setOnAction(e -> updateWeatherData());
        weatherToDatePicker.setOnAction(e -> updateWeatherData());
        trafficFromDatePicker.setOnAction(e -> updateTrafficData());
        trafficToDatePicker.setOnAction(e -> updateTrafficData());

        weatherStationFilter.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Integer>) c -> updateWeatherData());
        trafficStationFilter.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Integer>) c -> updateTrafficData());
    }

    public void loadPreferences() {
        // Load weather location and date preferences from the PreferenceManager
        String weatherLocation = PreferenceManager.getPreference("weather_location", "0"); // Default station ID is set to 0 or another default ID
        String weatherDate = PreferenceManager.getPreference("weather_date", LocalDate.now().toString());

        // Convert location from comma-separated string to a list of integers (station IDs)
        List<Integer> weatherLocationIds = new ArrayList<>();
        for (String id : weatherLocation.split(",")) {
            try {
                weatherLocationIds.add(Integer.parseInt(id.trim().replaceAll("[^0-9]", ""))); // Add each weather station ID to the list
            } catch (NumberFormatException e) {
                System.err.println("Invalid weather location ID in preferences, skipping: " + id);
            }
        }

        // Set the weather station filter selections based on saved preferences
        ObservableList<Integer> availableWeatherStationIds = FXCollections.observableArrayList(getAvailableWeatherStationIds());
        List<Integer> validWeatherLocationIds = new ArrayList<>();

        for (Integer locationId : weatherLocationIds) {
            if (availableWeatherStationIds.contains(locationId)) {
                validWeatherLocationIds.add(locationId);
            }
        }

        // Clear previous selections and select valid indices
        weatherStationFilter.getSelectionModel().clearSelection();
        for (Integer validId : validWeatherLocationIds) {
            // Select the valid station IDs by passing each ID individually
            weatherStationFilter.getSelectionModel().select(validId);
        }

        // Set weather date
        weatherFromDatePicker.setValue(LocalDate.parse(weatherDate)); // Set weather date

        // Load traffic location and date preferences from the PreferenceManager
        String trafficLocation = PreferenceManager.getPreference("traffic_location", "0"); // Default station ID is set to 0 or another default ID
        String trafficDate = PreferenceManager.getPreference("traffic_date", LocalDate.now().toString());

        // Convert location from comma-separated string to a list of integers (station IDs)
        List<Integer> trafficLocationIds = new ArrayList<>();
        for (String id : trafficLocation.split(",")) {
            try {
                trafficLocationIds.add(Integer.parseInt(id.trim().replaceAll("[^0-9]", ""))); // Add each traffic station ID to the list
            } catch (NumberFormatException e) {
                System.err.println("Invalid traffic location ID in preferences, skipping: " + id);
            }
        }

        // Set the traffic station filter selections based on saved preferences
        ObservableList<Integer> availableTrafficStationIds = FXCollections.observableArrayList(getAvailableTrafficStationIds());
        List<Integer> validTrafficLocationIds = new ArrayList<>();

        for (Integer locationId : trafficLocationIds) {
            if (availableTrafficStationIds.contains(locationId)) {
                validTrafficLocationIds.add(locationId);
            }
        }

        // Clear previous selections and select valid indices
        trafficStationFilter.getSelectionModel().clearSelection();
        for (Integer validId : validTrafficLocationIds) {
            // Select the valid traffic station IDs by passing each ID individually
            trafficStationFilter.getSelectionModel().select(validId);
        }

        // Set traffic date
        trafficFromDatePicker.setValue(LocalDate.parse(trafficDate)); // Set traffic date
    }

    private void updateData() {
        updateWeatherData();
        updateTrafficData();
    }

    public void updateWeatherData() {
        List<Integer> selectedWeatherStations = new ArrayList<>(weatherStationFilter.getSelectionModel().getSelectedItems());
        LocalDate weatherFromDate = weatherFromDatePicker.getValue();
        LocalDate weatherToDate = weatherToDatePicker.getValue();

        if (weatherFromDate == null || weatherToDate == null || weatherFromDate.isAfter(weatherToDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid weather date range selected.");
            alert.showAndWait();
            return;
        }

        LocalDateTime weatherFromDateTime = weatherFromDate.atStartOfDay();
        LocalDateTime weatherToDateTime = weatherToDate.plusDays(1).atStartOfDay();

        // Save preferences for weather
        PreferenceManager.savePreferences("weather_location", selectedWeatherStations.toString());
        PreferenceManager.savePreferences("weather_date", weatherFromDate.toString());

        Task<List<WeatherData>> fetchWeatherDataTask = new Task<>() {
            @Override
            protected List<WeatherData> call() throws Exception {
                return weatherService.fetchWeatherData(selectedWeatherStations, weatherFromDateTime, weatherToDateTime);
            }
        };

        fetchWeatherDataTask.setOnSucceeded(event -> {
            List<WeatherData> data = fetchWeatherDataTask.getValue();
            updateWeatherUI(data);
            updateCombinedLineChart(data, null); // Update only weather data in the line chart
        });

        new Thread(fetchWeatherDataTask).start();
    }

    public void updateTrafficData() {
        List<Integer> selectedTrafficStations = new ArrayList<>(trafficStationFilter.getSelectionModel().getSelectedItems());
        LocalDate trafficFromDate = trafficFromDatePicker.getValue();
        LocalDate trafficToDate = trafficToDatePicker.getValue();

        if (trafficFromDate == null || trafficToDate == null || trafficFromDate.isAfter(trafficToDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid traffic date range selected.");
            alert.showAndWait();
            return;
        }

        LocalDateTime trafficFromDateTime = trafficFromDate.atStartOfDay();
        LocalDateTime trafficToDateTime = trafficToDate.plusDays(1).atStartOfDay();

        // Save preferences for traffic
        PreferenceManager.savePreferences("traffic_location", selectedTrafficStations.toString());
        PreferenceManager.savePreferences("traffic_date", trafficFromDate.toString());

        Task<List<TrafficData>> fetchTrafficDataTask = new Task<>() {
            @Override
            protected List<TrafficData> call() throws Exception {
                return trafficService.fetchTrafficData(selectedTrafficStations, trafficFromDateTime, trafficToDateTime);
            }
        };

        fetchTrafficDataTask.setOnSucceeded(event -> {
            List<TrafficData> data = fetchTrafficDataTask.getValue();
            updateTrafficUI(data);
            updateCombinedLineChart(null, data); // Update only traffic data in the line chart
        });

        new Thread(fetchTrafficDataTask).start();
    }

    public void updateWeatherUI(List<WeatherData> weatherDataList) {
        ObservableList<WeatherData> weatherData = FXCollections.observableArrayList(weatherDataList);
        weatherTable.setItems(weatherData);

        weatherPieChart.getData().clear();
        int cold = 0, mild = 0, hot = 0;
        for (WeatherData data : weatherDataList) {
            double temp = data.getAirTemperature();
            if (temp < 10) {
                cold++;
            } else if (temp < 25) {
                mild++;
            } else {
                hot++;
            }
        }
        weatherPieChart.getData().add(new PieChart.Data("Cold (<10°C)", cold));
        weatherPieChart.getData().add(new PieChart.Data("Mild (10°C-25°C)", mild));
        weatherPieChart.getData().add(new PieChart.Data("Hot (>25°C)", hot));
    }

    public void updateTrafficUI(List<TrafficData> trafficDataList) {
        ObservableList<TrafficData> trafficData = FXCollections.observableArrayList(trafficDataList);
        trafficTable.setItems(trafficData);

        trafficPieChart.getData().clear();
        int slow = 0, normal = 0, fast = 0;
        for (TrafficData data : trafficDataList) {
            double speed = data.getSpeed();
            if (speed < 30) {
                slow++;
            } else if (speed < 70) {
                normal++;
            } else {
                fast++;
            }
        }
        trafficPieChart.getData().add(new PieChart.Data("Slow (<30 km/h)", slow));
        trafficPieChart.getData().add(new PieChart.Data("Normal (30-70 km/h)", normal));
        trafficPieChart.getData().add(new PieChart.Data("Fast (>70 km/h)", fast));
    }

    private void updateCombinedLineChart(List<WeatherData> weatherDataList, List<TrafficData> trafficDataList) {
        combinedLineChart.getData().clear();

        if (weatherDataList != null) {
            XYChart.Series<String, Number> tempSeries = new XYChart.Series<>();
            tempSeries.setName("Temperature");

            for (WeatherData data : weatherDataList) {
                tempSeries.getData().add(new XYChart.Data<>(data.getMeasurementTime(), data.getAirTemperature()));
            }
            combinedLineChart.getData().add(tempSeries);
        }

        if (trafficDataList != null) {
            XYChart.Series<String, Number> volumeSeries = new XYChart.Series<>();
            volumeSeries.setName("Traffic Volume");

            for (TrafficData data : trafficDataList) {
                volumeSeries.getData().add(new XYChart.Data<>(data.getMeasurementTime(), data.getVolume()));
            }
            combinedLineChart.getData().add(volumeSeries);
        }
    }

    private List<Integer> getAvailableWeatherStationIds() {
        return weatherService.fetchWeatherStationIds();
    }

    private List<Integer> getAvailableTrafficStationIds() {
        return trafficService.fetchTrafficStationIds();
    }

    @FXML
    public void switchToHomePage() throws IOException {
        Main.setRoot("/Views/HomePage.fxml");
    }

    @FXML
    public void switchToStatistics() throws IOException {
        Main.setRoot("/Views/Statistics.fxml");
    }

    @FXML
    public void switchToEconomicImpact() throws IOException {
        Main.setRoot("/Views/EconomicImpact.fxml");
    }

    @FXML
    public void LogOutProcess(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public ListView<Integer> getWeatherStationFilter() {
        return weatherStationFilter;
    }

    public DatePicker getWeatherFromDatePicker() {
        return weatherFromDatePicker;
    }

    public DatePicker getWeatherToDatePicker() {
        return weatherToDatePicker;
    }

    public ListView<Integer> getTrafficStationFilter() {
        return trafficStationFilter;
    }

    public DatePicker getTrafficFromDatePicker() {
        return trafficFromDatePicker;
    }

    public DatePicker getTrafficToDatePicker() {
        return trafficToDatePicker;
    }

    public TableView<WeatherData> getWeatherTable() {
        return weatherTable;
    }

    public TableView<TrafficData> getTrafficTable() {
        return trafficTable;
    }

}
