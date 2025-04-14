package com.financemanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for the Finance Manager application
 * This class initializes the JavaFX GUI and the database connection
 */
public class Main extends Application {
    
    private DataAccess dataAccess;
    
    /**
     * Main method that launches the JavaFX application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Check if this is a test run
        if (args.length > 0 && args[0].equals("test")) {
            // Run in test mode (handled by DataAccess.main)
            DataAccess.main(args);
        } else {
            try {
                // Launch the JavaFX application
                launch(args);
            } catch (Exception e) {
                System.err.println("Error starting JavaFX application: " + e.getMessage());
                e.printStackTrace();
                
                // If JavaFX fails to start, try launching the console version or show error
                System.err.println("JavaFX application could not be started.");
                System.err.println("Please ensure JavaFX runtime is available.");
                System.err.println("Try running with: java --module-path <path-to-javafx> --add-modules javafx.controls,javafx.fxml -jar finance-manager.jar");
                
                // Exit with error code
                System.exit(1);
            }
        }
    }
    
    /**
     * Initializes the JavaFX stage and sets up the main window
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Connect to the database
            dataAccess = new DataAccess();
            dataAccess.connect();
            dataAccess.createTablesIfNotExist();
            
            // Load the FXML file for the main window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/financemanager/main_window.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the data access object
            Controller controller = loader.getController();
            controller.setDataAccess(dataAccess);
            
            // Set up the stage
            primaryStage.setTitle("Финансовый менеджер");
            primaryStage.setScene(new Scene(root));
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            // Add CSS for styling
            Scene scene = primaryStage.getScene();
            scene.getStylesheets().add(getClass().getResource("/com/financemanager/styles.css").toExternalForm());
            
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Disconnects from the database when the application is stopped
     */
    @Override
    public void stop() {
        if (dataAccess != null) {
            dataAccess.disconnect();
        }
    }
}
