package com.financemanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * View class that manages the UI components of the application
 */
public class View {
    private Controller controller;
    private DataAccess dataAccess;

    /**
     * Initializes the view by loading the FXML and setting up the controller
     * @param primaryStage The main application window
     * @throws IOException If FXML file cannot be loaded
     */
    public void initialize(Stage primaryStage) throws IOException {
        // Initialize database connection
        dataAccess = new DataAccess();
        dataAccess.connect();
        dataAccess.createTablesIfNotExist();
        
        // Load FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/financemanager/main_window.fxml"));
        Parent root = loader.load();
        
        // Get the controller instance
        controller = loader.getController();
        
        // Initialize controller with data access
        controller.setDataAccess(dataAccess);
        // Note: We don't need to call controller.initialize() here as JavaFX already called it
        
        // Create scene and apply CSS
        Scene scene = new Scene(root);
        String cssPath = getClass().getResource("/com/financemanager/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);
        
        // Set the scene on the stage
        primaryStage.setScene(scene);
        
        // Handle application close event
        primaryStage.setOnCloseRequest(event -> {
            dataAccess.disconnect();
        });
    }
}
