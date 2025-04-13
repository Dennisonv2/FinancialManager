@echo off
echo ===== Создание .exe файла для приложения Финансовый Менеджер =====
echo.

REM Проверка наличия Java
java -version 2>NUL
if %ERRORLEVEL% NEQ 0 (
    echo [31mОшибка: Java не установлена. Пожалуйста, установите Java 17 или выше.[0m
    goto :error
)

REM Проверка наличия Maven
mvn -version 2>NUL
if %ERRORLEVEL% NEQ 0 (
    echo [31mОшибка: Maven не установлен. Пожалуйста, установите Maven 3.8.0 или выше.[0m
    goto :error
)

echo Очистка и сборка проекта...
call mvn clean package

if %ERRORLEVEL% NEQ 0 (
    echo [31mОшибка: Не удалось собрать проект.[0m
    goto :error
)

echo [32mСборка завершена успешно![0m

if exist "target\FinanceManager.exe" (
    echo [32mФайл .exe успешно создан: target\FinanceManager.exe[0m
) else (
    echo [33mПредупреждение: Файл .exe не был создан автоматически.[0m
    echo [33mВозможно, не настроен плагин Launch4j или отсутствуют зависимости.[0m
    echo.
    echo [33mПожалуйста, выберите вариант действий:[0m
    echo [33m1. Использовать jpackage для создания установщика (рекомендуется, требуется JDK 14+)[0m
    echo [33m2. Использовать Launch4j GUI для создания .exe файла[0m
    echo [33m3. Продолжить без создания .exe (использовать JAR-файл)[0m
    
    choice /C 123 /M "Выберите вариант (1-3): "
    
    if %ERRORLEVEL% EQU 1 (
        echo.
        echo Создание установщика с помощью jpackage...
        
        REM Проверка наличия jpackage
        jpackage --version 2>NUL
        if %ERRORLEVEL% NEQ 0 (
            echo [31mОшибка: jpackage не найден. Убедитесь, что у вас установлен JDK 14 или выше.[0m
            goto :error
        )
        
        jpackage --input target/ ^
          --main-jar FinanceManager-1.0-SNAPSHOT.jar ^
          --main-class com.financemanager.Main ^
          --name "FinансовыйМенеджер" ^
          --app-version 1.0 ^
          --win-menu ^
          --win-shortcut
          
        if %ERRORLEVEL% NEQ 0 (
            echo [31mОшибка: Не удалось создать установщик с помощью jpackage.[0m
            goto :error
        )
        
        echo [32mУстановщик успешно создан![0m
    )
    
    if %ERRORLEVEL% EQU 2 (
        echo.
        echo [33mДля использования Launch4j GUI:[0m
        echo [33m1. Запустите Launch4j[0m
        echo [33m2. Укажите JAR файл: %CD%\target\FinanceManager-1.0-SNAPSHOT.jar[0m
        echo [33m3. Укажите выходной .exe файл: %CD%\target\FinanceManager.exe[0m
        echo [33m4. Укажите Main класс: com.financemanager.Main[0m
        echo [33m5. Укажите минимальную версию JRE: 17.0.0[0m
        echo [33m6. Нажмите "Build wrapper"[0m
    )
    
    if %ERRORLEVEL% EQU 3 (
        echo.
        echo [33mДля запуска JAR-файла используйте команду:[0m
        echo [33mjava -jar target\FinanceManager-1.0-SNAPSHOT.jar[0m
    )
)

echo.
echo Для получения дополнительной информации о создании .exe файла, 
echo смотрите docs\CREATE_EXE_GUIDE.md
echo.
goto :eof

:error
echo.
echo [31mПроцесс завершился с ошибкой.[0m
echo.
pause
exit /b 1

:eof
pause
