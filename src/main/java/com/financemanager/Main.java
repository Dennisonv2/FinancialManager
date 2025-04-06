package com.financemanager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point of the Finance Manager application
 */
public class Main extends Application {

    /**
     * Main method that launches the JavaFX application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX start method that initializes the application UI
     * @param primaryStage The primary stage for the application
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Create View instance and initialize it with the primary stage
            View view = new View();
            view.initialize(primaryStage);
            
            // Set application title
            primaryStage.setTitle("Personal Finance Manager");
            
            // Set minimum window size
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Show the window
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
