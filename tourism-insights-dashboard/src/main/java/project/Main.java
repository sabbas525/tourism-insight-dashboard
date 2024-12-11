package project;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * The Main class serves as the entry point for the JavaFX application, responsible for launching the user interface 
 * and managing scene transitions.
 * 
 * It overrides the start() method to initialize 
 * the primary stage and load the initial FXML layout for the main view of the application.
 * 
 *  Setting up and displaying the primary JavaFX window (Stage) with an initial scene.
 *  Loading the FXML files for different views dynamically, allowing for scene transitions within the application.
 *  Setting the title of the application window and providing the structure for switching between different views.
 * 
 */
public class Main extends Application {

    private static Scene scene;
    
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/Views/HomePage.fxml"));
        scene = new Scene(root);
        primaryStage.setTitle("Tourism Insights Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

