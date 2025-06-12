#!/bin/bash
# Finance Manager launcher for Linux/Mac

echo "=== Starting Finance Manager Application ==="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java not found."
    echo "Please install Java 17 or later and try again."
    exit 1
fi

# Set path to the JAR file
JAR_PATH="target/FinanceManager-executable-1.0-SNAPSHOT.jar"

# Check if JAR file exists
if [ ! -f "$JAR_PATH" ]; then
    echo "Error: Application JAR file not found at $JAR_PATH"
    echo "Please build the application first using 'mvn clean package'"
    exit 1
fi

# Run the application with JavaFX modules
echo "Starting Finance Manager..."

# Try with JavaFX path if JAVAFX_HOME is set
if [ ! -z "$JAVAFX_HOME" ]; then
    java --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.fxml -jar "$JAR_PATH"
else
    # Try with Java's module path
    java --module-path "$JAVA_HOME/lib" --add-modules javafx.controls,javafx.fxml -jar "$JAR_PATH"
    
    # If that fails, try direct jar launch as a fallback
    if [ $? -ne 0 ]; then
        echo ""
        echo "=== ALTERNATIVE LAUNCH METHOD ==="
        echo "Trying alternative launch method with system JavaFX..."
        echo ""
        java -jar "$JAR_PATH"
    fi
fi

echo "Application closed."
