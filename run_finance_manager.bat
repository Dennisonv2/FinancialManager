@echo off
echo Запуск Менеджера Финансов...

REM Check if Java is installed
java -version > nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java не установлен или отсутствует в PATH. Пожалуйста, установите Java 17 или выше.
    pause
    exit /b 1
)

REM Run the application with JavaFX
echo Запуск приложения...

REM Try to run with Maven if available
mvn -version > nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Запуск через Maven...
    mvn compile && mvn javafx:run
) else (
    REM Check if the JAR file exists
    if exist "target\FinanceManager-1.0-SNAPSHOT.jar" (
        echo Запуск через JAR файл...
        java --module-path lib --add-modules javafx.controls,javafx.fxml -jar target\FinanceManager-1.0-SNAPSHOT.jar
    ) else (
        echo JAR файл не найден. Пожалуйста, соберите проект с помощью Maven: mvn clean package
        pause
        exit /b 1
    )
)
