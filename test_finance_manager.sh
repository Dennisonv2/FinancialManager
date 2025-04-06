#!/bin/bash

echo "===== Testing Finance Manager Application ====="

# Make sure Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Clean and compile the project
echo "Compiling the project..."
mvn clean compile

# Check compile success
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please fix the errors and try again."
    exit 1
fi

echo "Compilation successful."

# Test database connection
echo "Testing database connection..."
mvn exec:java -Dexec.mainClass="com.financemanager.DataAccess" -Dexec.args="test" -q

# Check test result
if [ $? -ne 0 ]; then
    echo "Database connection test failed. Please check your database setup."
    exit 1
fi

echo "Database connection test passed."

echo "===== All tests passed! ====="
echo "The Finance Manager application is ready to use."
echo "Run './run_finance_manager.sh' to start the application."
