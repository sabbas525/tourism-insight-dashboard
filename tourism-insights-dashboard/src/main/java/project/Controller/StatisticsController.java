package project.Controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project.Main;
import project.Model.TripDataAndDuration;
import project.Model.TripStatistics;
import project.Model.VisitorStatistics;
import project.ServiceFacade.StatisticsServiceFacade;
import project.Common.PreferenceManager;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * The StatisticsController is responsible for handling user interactions and controlling the data flow in the statistics
 * screen of the application. It manages the filtering and display of trip statistics based on the user's selected filters
 * such as season, trip type, and year.
 * 
 * It interacts with the StatisticsServiceFacade to fetch and process data, such as trips and 
 * visitor related data. Additionally, it handles user preferences and updates the UI components with relevant information.
 * 
 */
public class StatisticsController {
    
        @FXML
        private ComboBox<String> seasonFilter;
    
        @FXML
        private ComboBox<String> tripTypeFilter;
    
        @FXML
        private ComboBox<String> yearFilter;
    
        @FXML
        private BarChart<String, Number> tripCountBarChart;
    
        @FXML
        private PieChart visitorPieChart;
    
        @FXML
        private TableView<TripDataAndDuration> statisticsTable;
    
        @FXML
        private TableColumn<TripDataAndDuration, String> destinationColumn;
    
        @FXML
        private TableColumn<TripDataAndDuration, Integer> visitorCountColumn;
    
        @FXML
        private TableColumn<TripDataAndDuration, Integer> durationColumn;
    
         @FXML
        private RadioButton ageRadioButton;
    
        @FXML
        private RadioButton genderRadioButton;
    
        private ToggleGroup purposeToggleGroup; 
    
        private String selectedTripType;
    
    
        private List<TripStatistics> tripDataByDestination = null;
        private List<VisitorStatistics> visiotrData = null;
        private List<TripDataAndDuration> tripDataAndDuration = null;
    
        private StatisticsServiceFacade serviceFacade;
        private PreferenceManager preferenceManager;
    
        public StatisticsController(){
            this.serviceFacade = new StatisticsServiceFacade();
            this.preferenceManager = new PreferenceManager();
        }
    
        public void initialize() {

            // Initialize ComboBoxes 
            seasonFilter.setItems(FXCollections.observableArrayList("Spring", "Summer", "Autumn"));
            tripTypeFilter.setItems(FXCollections.observableArrayList( "Leisure Trip Abroad", 
            "Cruise", 
            "Same-Day Visit Abroad", 
            "Domestic Leisure Trip", 
            "Domestic Visit"));
            yearFilter.setItems(FXCollections.observableArrayList("2024", "2023", "2022", "2021"));
    
            seasonFilter.setValue(preferenceManager.getPreference("season", "Spring"));
            tripTypeFilter.setValue(preferenceManager.getPreference("tripType", "Leisure Trip Abroad"));
            yearFilter.setValue(preferenceManager.getPreference("year", "2024"));

            purposeToggleGroup = new ToggleGroup();

            // Add the radio buttons to the group
            ageRadioButton.setToggleGroup(purposeToggleGroup);
            genderRadioButton.setToggleGroup(purposeToggleGroup);
            ageRadioButton.setSelected(true);

            tripDataByDestination = this.serviceFacade.getTripsByDestination();
            visiotrData = this.serviceFacade.getVisitorStatistics();
            tripDataAndDuration = this.serviceFacade.getTripsAndDuration();

            seasonFilter.setOnAction(event -> {
                String selectedSeason = seasonFilter.getValue();
                preferenceManager.savePreferences("season", selectedSeason);
                updateBarChart(selectedSeason);
            });

            tripTypeFilter.setOnAction(event -> {
                this.selectedTripType = tripTypeFilter.getValue();
                preferenceManager.savePreferences("tripType", selectedTripType);
                RadioButton selectedRadioButton = (RadioButton) purposeToggleGroup.getSelectedToggle();
                handleRadioButtonChange(this.selectedTripType, selectedRadioButton.getText());
            });

            yearFilter.setOnAction(event -> {
                String selectedYear = yearFilter.getValue();
                preferenceManager.savePreferences("year", selectedYear);
                updateStatisticsTable(selectedYear);
            });

            purposeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    RadioButton selectedRadioButton = (RadioButton) purposeToggleGroup.getSelectedToggle();
                    handleRadioButtonChange(this.selectedTripType, selectedRadioButton.getText());
                }
            });

            updateBarChart(preferenceManager.getPreference("season", "Spring"));
            updateAgeGroupPieChart(preferenceManager.getPreference("tripType", "Leisure Trip Abroad"));
            updateStatisticsTable(preferenceManager.getPreference("year", "2024"));
        }

    private void updateBarChart(String selectedSeason) {
        tripCountBarChart.getData().clear();

        List<TripStatistics> filteredData = this.serviceFacade.getTripsBySeason(tripDataByDestination, selectedSeason);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Trips Count for " + seasonFilter.getValue());

        for (TripStatistics tripStatistics : filteredData) {
            series.getData().add(new XYChart.Data<>(tripStatistics.getDestination(), tripStatistics.getTripCount()));
        }

        tripCountBarChart.getData().add(series);
    }

    private void handleRadioButtonChange(String selectedTripType, String selectedOption) {
       if(selectedOption.equals("Age Group")){
        updateAgeGroupPieChart(selectedTripType);
       }
       else{
        updateGenderPieChart(selectedTripType);
       }
    }
    
    private void updateAgeGroupPieChart(String selectedTripType) {
        visitorPieChart.getData().clear();
        visitorPieChart.setTitle("Visitors by Age Group");

        List<VisitorStatistics> filteredData = this.serviceFacade.getVisitorsByTripType(visiotrData, selectedTripType);
        // Create a map to hold total percentages for each trip type
        Map<String, Double> ageCounts = new HashMap<>();

        // Add slices to the pie chart based on total percentages by trip type
        for (VisitorStatistics stats : filteredData) {
            String ageGroup = stats.getAgeGroup();
            Double percentage = stats.getPercentage();

            ageCounts.put(ageGroup, ageCounts.getOrDefault(ageGroup, 0.0) + percentage);
        }

         // Add slices to the pie chart based on total percentages by trip type
        for (Map.Entry<String, Double> entry : ageCounts.entrySet()) {
            String ageGroup = entry.getKey();
            Double totalPercentage = entry.getValue();

            // Add data to pie chart
            PieChart.Data slice = new PieChart.Data(ageGroup, totalPercentage);
            visitorPieChart.getData().add(slice);
        }  
    }

    private void updateGenderPieChart(String selectedTripType) {
        visitorPieChart.getData().clear();
        visitorPieChart.setTitle("Visitors by Gender");
        
        List<VisitorStatistics> filteredData = this.serviceFacade.getVisitorsByTripType(visiotrData, selectedTripType);
        // Create a map to hold total percentages for each trip type
        Map<String, Double> genderCounts = new HashMap<>();

        // Add slices to the pie chart based on total percentages by trip type
        for (VisitorStatistics stats : filteredData) {
            String gender = stats.getGender();
            Double percentage = stats.getPercentage();

            genderCounts.put(gender, genderCounts.getOrDefault(gender, 0.0) + percentage);
        }

         // Add slices to the pie chart based on total percentages by trip type
        for (Map.Entry<String, Double> entry : genderCounts.entrySet()) {
            String gender = entry.getKey();
            Double totalPercentage = entry.getValue();

            // Add data to pie chart
            PieChart.Data slice = new PieChart.Data(gender, totalPercentage);
            visitorPieChart.getData().add(slice);
        }  
    }

    private void updateStatisticsTable(String selectedYear) {
        // Initialize Table Columns
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        visitorCountColumn.setCellValueFactory(new PropertyValueFactory<>("overnightTrips"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("averageLengthOfTrip"));

        List<TripDataAndDuration> filteredData = this.serviceFacade.getTripsByYear(tripDataAndDuration, selectedYear);
        ObservableList<TripDataAndDuration> filteredStatistics = FXCollections.observableArrayList();

        for (TripDataAndDuration trip : filteredData) {
            String destination = trip.getDestination();
            int numberOfTrips = trip.getOvernightTrips();
            Double averageStay = trip.getAverageLengthOfTrip();
    
            filteredStatistics.add(new TripDataAndDuration(selectedYear, destination, averageStay, numberOfTrips));
        }
    
        statisticsTable.setItems(filteredStatistics);
    }

    @FXML
    public void LogOutProcess(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToHomePage() throws IOException {
        Main.setRoot("/Views/HomePage.fxml");
    }

    @FXML
    public void switchToWeather() throws IOException {
        Main.setRoot("/Views/Weather.fxml");
    }
    @FXML
    public void switchToEconomicImpact() throws IOException {
        Main.setRoot("/Views/EconomicImpact.fxml");
    }
}
