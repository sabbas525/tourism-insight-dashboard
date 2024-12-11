package project.ServiceFacade;

import project.Service.TrafficService;
import project.Service.WeatherService;
import project.Model.WeatherData;
import project.Model.TrafficData;
import project.Common.PreferenceManager;

import java.time.LocalDateTime;
import java.util.List;

public class WeatherServiceFacade {
    private WeatherService weatherService;
    private TrafficService trafficService;

    public WeatherServiceFacade() {
        this.weatherService = new WeatherService();
        this.trafficService = new TrafficService();
    }

    public void setPreferences(String location, String date) {
        PreferenceManager.savePreferences("location", location);
        PreferenceManager.savePreferences("date", date);
    }

    public List<WeatherData> getWeatherData(List<Integer> stationIds, LocalDateTime fromDate, LocalDateTime toDate) {
        return weatherService.fetchWeatherData(stationIds, fromDate, toDate);
    }

    public List<TrafficData> getTrafficData(List<Integer> stationIds, LocalDateTime fromDate, LocalDateTime toDate) {
        return trafficService.fetchTrafficData(stationIds, fromDate, toDate);
    }
}
