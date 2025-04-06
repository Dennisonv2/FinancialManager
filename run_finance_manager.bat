@echo off
echo Starting Finance Manager...

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed or not in PATH. Please install Java 17 or higher.
    pause
    exit /b 1
)

REM Run the application
echo Choose a mode:
echo 1. GUI Mode
echo 2. Console Mode
set /p mode="Enter your choice (1/2): "

if "%mode%"=="1" (
    echo Starting GUI mode...
    mvn compile && mvn javafx:run
) else if "%mode%"=="2" (
    echo Starting Console mode...
    mvn compile && mvn exec:java -Dexec.mainClass="com.financemanager.ConsoleMain"
) else (
    echo Invalid choice. Please enter 1 or 2.
    pause
    exit /b 1
)

pause
