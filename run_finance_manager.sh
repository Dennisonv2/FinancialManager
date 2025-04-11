#!/bin/bash

echo "Запуск Менеджера Финансов..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java не установлен или отсутствует в PATH. Пожалуйста, установите Java 17 или выше."
    exit 1
fi

# Run the application with JavaFX
echo "Запуск приложения..."

# Try to run with Maven if available
if command -v mvn &> /dev/null; then
    echo "Запуск через Maven..."
    mvn compile && mvn javafx:run
else
    # Check if the JAR file exists
    if [ -f "target/FinanceManager-1.0-SNAPSHOT.jar" ]; then
        echo "Запуск через JAR файл..."
        java --module-path lib --add-modules javafx.controls,javafx.fxml -jar target/FinanceManager-1.0-SNAPSHOT.jar
    else
        echo "JAR файл не найден. Пожалуйста, соберите проект с помощью Maven: mvn clean package"
        exit 1
    fi
fi
