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
     * Initializes the JavaFX stage and sets up the welcome screen
     * @param primaryStage The primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load the welcome screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/financemanager/welcome_screen.fxml"));
            Parent root = loader.load();
            
            // Set up the stage with welcome screen
            primaryStage.setTitle("Finance Manager - Добро пожаловать");
            Scene scene = new Scene(root, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/com/financemanager/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Error initializing welcome screen: " + e.getMessage());
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
