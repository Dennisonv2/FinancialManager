@echo off
echo === Starting Finance Manager - Simple Launcher ===

java -jar target\FinanceManager-executable-1.0-SNAPSHOT.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error occurred while starting the application.
    echo Please check Java installation (Java 17+ required).
    echo.
    pause
)
