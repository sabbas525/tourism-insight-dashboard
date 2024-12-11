package project.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * The TrafficData class represents traffic measurement data from a specific station at a given time. 
 * It includes the station ID, the time the measurement was taken, the traffic volume, and the average speed.
 * enables easy data binding with UI components.
 */
public class TrafficData {
    private final SimpleIntegerProperty stationId;
    private final SimpleStringProperty measurementTime;
    private final SimpleDoubleProperty volume;
    private final SimpleDoubleProperty speed;

    public TrafficData(int stationId, String measurementTime, double volume, double speed) {
        this.stationId = new SimpleIntegerProperty(stationId);
        this.measurementTime = new SimpleStringProperty(measurementTime);
        this.volume = new SimpleDoubleProperty(volume);
        this.speed = new SimpleDoubleProperty(speed);
    }

    public int getStationId() {
        return stationId.get();
    }

    public String getMeasurementTime() {
        return measurementTime.get();
    }

    public double getVolume() {
        return volume.get();
    }

    public double getSpeed() {
        return speed.get();
    }
}
