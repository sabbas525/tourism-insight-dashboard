package project.Model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * This class represents the economic impact data model for 
 * region, year, and type, along with its associated value. This class is designed 
 * using properties for data binding and UI integration.
 */
public class EconomicImpactData {
    private SimpleStringProperty product;
    private SimpleStringProperty region;
    private SimpleStringProperty year;
    private SimpleStringProperty type;
    private SimpleDoubleProperty value;

    public EconomicImpactData(String product, String region, String year, String type, double value) {
        this.product = new SimpleStringProperty(product);
        this.region = new SimpleStringProperty(region);
        this.year = new SimpleStringProperty(year);
        this.type = new SimpleStringProperty(type);
        this.value = new SimpleDoubleProperty(value);
    }

    public String getProduct() { return product.get(); }
    public void setProduct(String product) { this.product.set(product); }

    public String getRegion() { return region.get(); }
    public void setRegion(String region) { this.region.set(region); }

    public String getYear() { return year.get(); }
    public void setYear(String year) { this.year.set(year); }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }

    public double getValue() { return value.get(); }
    public void setValue(double value) { this.value.set(value); }
    public SimpleStringProperty productProperty() { return product; }
    public SimpleStringProperty regionProperty() { return region; }
    public SimpleStringProperty yearProperty() { return year; }
    public SimpleStringProperty typeProperty() { return type; }
    public SimpleDoubleProperty valueProperty() { return value; }

}