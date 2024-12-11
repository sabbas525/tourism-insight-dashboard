package project.Controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import project.Main;
import project.Common.PreferenceManager;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.Model.*;
import project.ServiceFacade.HomeServiceFacade;

/**
 * The HomeController is responsible for managing the data and logic behind the home page of the application. It interacts
 * with the HomeServiceFacade to fetch and process tourism-related data, such as the number of tourists and 
 * their purpose of visit. Additionally, it handles user preferences and updates the UI components with relevant information.
 * 
 * It also manages the filtering and display of tourism statistics based on the user's selected filters.
 * 
 */
public class HomeController {

    private List<TouristData> touristsData = new ArrayList<>();
    private List<TouristDatabyPurpose> touristDatabyPurpose = new ArrayList<>();
    private HomeServiceFacade homeService;
    private PreferenceManager preferenceManager;

    public HomeController(){
        homeService = new HomeServiceFacade();
        preferenceManager = new PreferenceManager();
    }
    
    @FXML
    private LineChart<String, Number> visitorLineChart;

    @FXML
    private PieChart visitorPieChart;

    @FXML
    private PieChart purposePiChart;

    @FXML
    private LineChart<String, Number> purposeLineChart;

    @FXML
    private Label purposeLvl;

    @FXML
    private ChoiceBox<String> locationsDropdown;

    @FXML
    private Label placeNamelvl;

    @FXML
    private Label totalVisitorNumLvl;

    @FXML
    private Label visitorPercentageLvl;
    
    @FXML
    public void initialize() {
        touristsData = homeService.getTouriseDataList();
        touristDatabyPurpose = homeService.getTourisePurposeDataList();

        InitializeLocationcomboBox();
        InitializeTouristData();
        InitializeTouristDataByPurpose();
    }

    private void InitializeLocationcomboBox()
    {
        List<String> regionNames = new ArrayList<>();
        for (TouristData data : touristsData) {
            regionNames.add(data.getRegionName());
        }

        locationsDropdown.setItems(FXCollections.observableArrayList(regionNames).sorted());
        locationsDropdown.setValue(preferenceManager.getPreference("selectedLocation", "Finland"));
    }

    private void InitializeTouristData()
    {
        var selectedLocation = preferenceManager.getPreference("selectedLocation", "Finland");

        var filteredTouristData = homeService.getLocationWiseTouriseDataList((touristsData), selectedLocation);

        var filteredTouristyearData = homeService.getLocationAndYearWiseTouriseDataList(selectedLocation);

        UpdateTouristDataByLocation(filteredTouristData, selectedLocation);
        UpdateVisitorLineChart(filteredTouristyearData, selectedLocation);
        UpdateVisitorPieChart(filteredTouristyearData, selectedLocation);
    }
    
    private void InitializeTouristDataByPurpose()
    {
        var filterFor_1_6 = homeService.getPurposeWiseTouriseDataList((touristDatabyPurpose), "1,6");

        purposePiChart.getData().clear();
        Map<String, Double> dataCounts = new HashMap<>();

        for (TouristDatabyPurpose data : filterFor_1_6) {
            String purposeGroup = data.getPurpose();
            Double touristNo = (Double) data.getTrouristsNo();

            System.out.println(purposeGroup);

            dataCounts.put(purposeGroup, dataCounts.getOrDefault(purposeGroup, 0.0) + touristNo);
        }

        for (Map.Entry<String, Double> entry : dataCounts.entrySet()) {
            String purposeGroup = entry.getKey();
            Double totaltouristNo = entry.getValue();

            PieChart.Data slice = new PieChart.Data(purposeGroup, totaltouristNo);
            purposePiChart.getData().add(slice);
        } 
        purposePiChart.setLabelLineLength(10); 
        purposePiChart.setTitle("Personal & Work");

        for (PieChart.Data data : purposePiChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);

            // Optional: Highlight pie slice on hover
            data.getNode().setOnMouseEntered(event -> 
                data.getNode().setStyle("-fx-pie-color: #ff9933;"));
            data.getNode().setOnMouseExited(event -> 
                data.getNode().setStyle(""));
        }

        var filterFor_2_3_4_5 = homeService.getPurposeWiseTouriseDataList((touristDatabyPurpose), "2,3,4,5");

        var filterFor_7_8_9_10 = homeService.getPurposeWiseTouriseDataList((touristDatabyPurpose), "7,8,9,10");

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Personal - Sub Purposes");
        for (TouristDatabyPurpose obj : filterFor_2_3_4_5) {
            series1.getData().add(new XYChart.Data<>(obj.getPurposeID(), obj.getTrouristsNo()));
        }

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Work - Sub Purposes");
        for (TouristDatabyPurpose obj : filterFor_7_8_9_10) {
            series2.getData().add(new XYChart.Data<>(obj.getPurposeID(), obj.getTrouristsNo()));
        }
        purposeLineChart.getData().clear(); 
        purposeLineChart.getData().addAll(series1, series2);
        
        for (XYChart.Series<String, Number> series : purposeLineChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip(data.getYValue().toString());
                Tooltip.install(data.getNode(), tooltip);
        
                data.getNode().setOnMouseEntered(event -> 
                    data.getNode().setStyle("-fx-bar-fill: #ff9933;")); 
                data.getNode().setOnMouseExited(event -> 
                    data.getNode().setStyle("")); 
            }
        }

        StringBuilder mapString = new StringBuilder();
        for (TouristDatabyPurpose obj : filterFor_2_3_4_5) {
            mapString.append(obj.getPurposeID()).append(": ").append(obj.getPurpose()).append("\n");
        }

        mapString.append("\n");

        for (TouristDatabyPurpose obj : filterFor_7_8_9_10) {
            mapString.append(obj.getPurposeID()).append(": ").append(obj.getPurpose()).append("\n");
        }
        purposeLvl.setText(mapString.toString());
    }

    @FXML
    private void LoadTouristDataByLocation() {
        var selectedLocation = locationsDropdown.getValue();
        preferenceManager.savePreferences("selectedLocation", selectedLocation);

        var selectedLocationCode = GetRegionCode((selectedLocation));
        preferenceManager.savePreferences("selectedLocationCode", selectedLocationCode);
        
        var filteredTouristData = homeService.getLocationWiseTouriseDataList((touristsData), preferenceManager.getPreference("selectedLocation", "Finland"));

        var filteredTouristyearData = homeService.getLocationAndYearWiseTouriseDataList(preferenceManager.getPreference("selectedLocationCode", "Finland"));

        UpdateTouristDataByLocation(filteredTouristData, preferenceManager.getPreference("selectedLocation", "Finland"));
        UpdateVisitorLineChart(filteredTouristyearData, preferenceManager.getPreference("selectedLocation", "Finland"));
        UpdateVisitorPieChart(filteredTouristyearData, preferenceManager.getPreference("selectedLocation", "Finland"));
    }
    
    private void UpdateTouristDataByLocation(List<TouristData> touristsData, String selectedLocation) {
       
        try 
        {
            if(selectedLocation.equals("Finland"))
            {
                if (!touristsData.isEmpty()) 
                {
                    totalVisitorNumLvl.setText(String.valueOf(touristsData.get(0).getTrouristsNo()));
                }
                else
                {
                    totalVisitorNumLvl.setText("---");
                }
                visitorPercentageLvl.setText("---");
            }
            else
            {
                placeNamelvl.setText(selectedLocation);
                if (!touristsData.isEmpty()) 
                {
                    totalVisitorNumLvl.setText(String.valueOf(touristsData.get(1).getTrouristsNo()));
                    Float percentage =  ((float)touristsData.get(1).getTrouristsNo() / (float)touristsData.get(0).getTrouristsNo()) * 100;
                    visitorPercentageLvl.setText(String.format("%.2f", percentage) + "%");
                }
                else
                {
                    totalVisitorNumLvl.setText("---");
                    visitorPercentageLvl.setText("---");
                }
                }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateVisitorLineChart(List<TouristYearData> touristyearData, String selectedLocation)
    {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Visitors History - " + selectedLocation);
        for (TouristYearData obj : touristyearData) {
            series.getData().add(new XYChart.Data<>(obj.getYear(), obj.getTrouristsNo()));
        }
        visitorLineChart.getData().clear(); 
        visitorLineChart.getData().add(series);
        
        for (XYChart.Data<String, Number> data : series.getData()) {
            Tooltip tooltip = new Tooltip(data.getYValue().toString());
            Tooltip.install(data.getNode(), tooltip);

            data.getNode().setOnMouseEntered(event -> 
                data.getNode().setStyle("-fx-bar-fill: #ff9933;"));
            data.getNode().setOnMouseExited(event -> 
                data.getNode().setStyle(""));
        }

    }

    private void UpdateVisitorPieChart(List<TouristYearData> touristyearData, String selectedLocation)
    {
        visitorPieChart.getData().clear();
        Map<String, Double> dataCounts = new HashMap<>();

        for (TouristYearData data : touristyearData) {
            String yearGroup = data.getYear();
            Double touristNo = (Double) data.getTrouristsNo();

            dataCounts.put(yearGroup, dataCounts.getOrDefault(yearGroup, 0.0) + touristNo);
        }

        for (Map.Entry<String, Double> entry : dataCounts.entrySet()) {
            String yearGroup = entry.getKey();
            Double totaltouristNo = entry.getValue();

            PieChart.Data slice = new PieChart.Data(yearGroup, totaltouristNo);
            visitorPieChart.getData().add(slice);
        }  
        visitorPieChart.setTitle("Yearly Data - " + selectedLocation);

        for (PieChart.Data data : visitorPieChart.getData()) {
            Tooltip tooltip = new Tooltip(data.getName() + ": " + (int) data.getPieValue());
            Tooltip.install(data.getNode(), tooltip);

            data.getNode().setOnMouseEntered(event -> 
                data.getNode().setStyle("-fx-pie-color: #ff9933;"));
            data.getNode().setOnMouseExited(event -> 
                data.getNode().setStyle(""));
        }
    }

    private String GetRegionCode(String selectedRegion) 
    {
        String regionCode = "";
        if(selectedRegion != null)
        {
            for (TouristData touristData : touristsData) {
                if (touristData.getRegionName().equalsIgnoreCase(selectedRegion)) {
                    regionCode =  touristData.getRegionID();
                }
            }
         }

        return regionCode;
    }

    @FXML
    public void LogOutProcess(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void switchToStatistics() throws IOException {
        Main.setRoot("/Views/Statistics.fxml");
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


