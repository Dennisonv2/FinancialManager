#!/bin/bash

echo "Starting Finance Manager..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed or not in PATH. Please install Java 17 or higher."
    exit 1
fi

# Run the application
echo "Choose a mode:"
echo "1. GUI Mode"
echo "2. Console Mode"
read -p "Enter your choice (1/2): " mode

if [ "$mode" = "1" ]; then
    echo "Starting GUI mode..."
    mvn compile && mvn javafx:run
elif [ "$mode" = "2" ]; then
    echo "Starting Console mode..."
    mvn compile && mvn exec:java -Dexec.mainClass="com.financemanager.ConsoleMain"
else
    echo "Invalid choice. Please enter 1 or 2."
    exit 1
fi
