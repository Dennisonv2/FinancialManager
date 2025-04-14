package com.financemanager;

/**
 * Launcher class for the Finance Manager application
 * This class serves as the main entry point for both
 * JAR and module-based execution
 */
public class MainApp {
    
    /**
     * Main method that delegates to the actual Main class
     * This indirection helps with JavaFX deployment
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
