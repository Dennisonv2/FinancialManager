@echo off
:: Script to create executable for Finance Manager App

echo === Finance Manager - EXE Creation Tool ===
echo.

:: Check if Maven is installed
mvn -v >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven not found.
    echo Please install Maven and try again.
    pause
    exit /b 1
)

:: Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found.
    echo Please install Java 17 or later and try again.
    pause
    exit /b 1
)

echo Step 1: Cleaning and packaging the application...
call mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven build failed.
    pause
    exit /b 1
)

echo.
echo Step 2: Checking that executable has been created...

:: Check if the executable JAR exists
set JAR_PATH=target\FinanceManager-executable-1.0-SNAPSHOT.jar
if not exist "%JAR_PATH%" (
    echo Error: Executable JAR not found at %JAR_PATH%
    pause
    exit /b 1
)

:: Check if the EXE exists
set EXE_PATH=target\FinanceManager.exe
if not exist "%EXE_PATH%" (
    echo Warning: EXE file was not created.
    echo Only JAR file is available for distribution.
    goto CONTINUE_WITHOUT_EXE
)

echo.
echo Step 3: Success! EXE file created successfully.
echo.
echo The executable file is located at: %EXE_PATH%
echo.
echo You can now copy this file to any Windows computer.
echo NOTE: The user must have Java 17 or later installed.
goto END

:CONTINUE_WITHOUT_EXE
echo.
echo You can still use the JAR file at: %JAR_PATH%
echo Use run_finance_manager.bat to launch the application.
echo.

:END
echo.
echo === Creation Process Complete ===
pause
