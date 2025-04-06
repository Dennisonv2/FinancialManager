package com.financemanager;

/**
 * Alternative main class to simplify launching from IDE
 * This class helps with JavaFX module errors in some IDEs
 */
public class MainApp {
    /**
     * Main method that delegates to the JavaFX Application main
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // This wrapper class makes it easier to run from IDEs
        // that have problems with JavaFX modules
        Main.main(args);
    }
}
