@echo off
echo ===== Запуск приложения Финансовый Менеджер =====
echo.

REM Проверка наличия Java
java -version 2>NUL
if %ERRORLEVEL% NEQ 0 (
    echo [31mОшибка: Java не установлена. Пожалуйста, установите Java 17 или выше.[0m
    goto :error
)

REM Проверка наличия JAR-файла
if not exist "target\FinanceManager-1.0-SNAPSHOT.jar" (
    echo JAR-файл не найден. Запуск сборки проекта...
    
    REM Проверка наличия Maven
    mvn -version 2>NUL
    if %ERRORLEVEL% NEQ 0 (
        echo [31mОшибка: Maven не установлен. Пожалуйста, установите Maven 3.8.0 или выше.[0m
        goto :error
    )
    
    call mvn clean package
    
    if %ERRORLEVEL% NEQ 0 (
        echo [31mОшибка: Не удалось собрать проект.[0m
        goto :error
    )
)

echo Запуск приложения...
java --module-path "target\dependency" --add-modules javafx.controls,javafx.fxml -jar target\FinanceManager-1.0-SNAPSHOT.jar

if %ERRORLEVEL% NEQ 0 (
    echo [31mОшибка при запуске приложения.[0m
    echo [33mПопытка альтернативного запуска...[0m
    
    REM Альтернативный способ запуска в случае проблем с JavaFX
    java -jar target\FinanceManager-1.0-SNAPSHOT.jar
    
    if %ERRORLEVEL% NEQ 0 (
        echo [31mНе удалось запустить приложение.[0m
        goto :error
    )
)

goto :eof

:error
echo.
echo [31mПроцесс завершился с ошибкой.[0m
echo.
pause
exit /b 1

:eof
echo.
pause
