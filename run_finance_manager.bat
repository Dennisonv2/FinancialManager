@echo off
:: Finance Manager launcher for Windows
echo === Starting Finance Manager Application ===

:: Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found.
    echo Please install Java 17 or later and try again.
    pause
    exit /b 1
)

:: Set path to the JAR file
set JAR_PATH=target\FinanceManager-executable-1.0-SNAPSHOT.jar

:: Check if JAR file exists
if not exist "%JAR_PATH%" (
    echo Error: Application JAR file not found at %JAR_PATH%
    echo Please build the application first using 'mvn clean package'
    pause
    exit /b 1
)

:: Run the application with JavaFX modules
echo Starting Finance Manager...
java --module-path "%JAVA_HOME%\javafx-sdk-17\lib" --add-modules javafx.controls,javafx.fxml -jar %JAR_PATH%

:: If we get here, there was an error
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo === ALTERNATIVE LAUNCH METHOD ===
    echo Trying alternative launch method with system JavaFX...
    echo.
    java -jar %JAR_PATH%
)

pause
