module com.example.tourism.insights.dashboard {
    // Declare the required modules for dependencies
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires org.apache.xmlbeans;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires com.fasterxml.jackson.databind;
    requires java.xml;  // For XML processing
    requires java.net.http;
    requires javafx.web;
    requires com.google.gson;
    requires org.apache.httpcomponents.core5.httpcore5; 
    requires javafx.base;
    requires org.controlsfx.controls;
    requires java.prefs;

    // Optional: You might need to open some packages if reflection is used by libraries like Jackson or FXML
    opens project to com.fasterxml.jackson.databind, javafx.fxml;
    opens project.Controller to com.fasterxml.jackson.databind, javafx.fxml, javafx.base;
    opens project.Model to javafx.base;
    
    exports project;
}
