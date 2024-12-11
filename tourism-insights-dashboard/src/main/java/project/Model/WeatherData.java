package project.Model;

import javafx.beans.property.*;

/**
 * The WeatherData class represents weather-related data collected at a particular station at a specific time.
 * It includes information such as air temperature, wind speed, and precipitation.
 * 
 * This class is used to store weather data measurements from a station, such as air temperature, wind speed,
 * and precipitation levels at a given point in time.
 */
public class WeatherData {
    private final SimpleIntegerProperty stationId;
    private final SimpleStringProperty measurementTime;
    private final SimpleDoubleProperty airTemperature;
    private final SimpleDoubleProperty windSpeed;
    private final SimpleDoubleProperty precipitation;

    public WeatherData(int stationId, String measurementTime, double airTemperature, double windSpeed, double precipitation) {
        this.stationId = new SimpleIntegerProperty(stationId);
        this.measurementTime = new SimpleStringProperty(measurementTime);
        this.airTemperature = new SimpleDoubleProperty(airTemperature);
        this.windSpeed = new SimpleDoubleProperty(windSpeed);
        this.precipitation = new SimpleDoubleProperty(precipitation);
    }

    public int getStationId() {
        return stationId.get();
    }

    public String getMeasurementTime() {
        return measurementTime.get();
    }

    public double getAirTemperature() {
        return airTemperature.get();
    }

    public double getWindSpeed() {
        return windSpeed.get();
    }

    public double getPrecipitation() {
        return precipitation.get();
    }
}
