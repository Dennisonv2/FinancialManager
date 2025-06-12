#!/bin/bash

echo "===== Комплексное тестирование приложения Финансовый Менеджер ====="

# Функция для вывода статуса
function print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "\e[32m✓ УСПЕХ\e[0m"
    else
        echo -e "\e[31m✗ НЕУДАЧА\e[0m"
        exit 1
    fi
}

# Проверка окружения Java и Maven
echo -n "Проверка наличия Maven... "
if mvn -v &> /dev/null; then
    print_status 0
else
    print_status 1
    echo "Maven не найден. Пожалуйста, убедитесь, что Maven установлен."
    exit 1
fi

# Очистка и компиляция проекта
echo -n "Очистка и компиляция проекта... "
mvn clean compile > /dev/null 2>&1
print_status $?

# Проверка подключения к базе данных SQLite
echo -n "Проверка подключения к базе данных... "
./test_db_connection.sh > /dev/null 2>&1
print_status $?

# Проверка основных компонентов приложения
echo -n "Тестирование модели данных... "
mvn exec:java -Dexec.mainClass="com.financemanager.TestApp" -q > /dev/null 2>&1
print_status $?

# Создание тестовых данных
echo -n "Создание тестовых данных... "
./test_app.sh > /dev/null 2>&1
print_status $?

# Упаковка приложения в JAR
echo -n "Создание исполняемого JAR-файла... "
mvn package -DskipTests > /dev/null 2>&1
print_status $?

# Проверка наличия JAR-файла
echo -n "Проверка созданного JAR-файла... "
if [ -f "target/FinanceManager-1.0-SNAPSHOT.jar" ]; then
    print_status 0
    echo "JAR-файл успешно создан: target/FinanceManager-1.0-SNAPSHOT.jar"
else
    print_status 1
fi

echo ""
echo "===== Все тесты успешно пройдены! ====="
echo ""
echo "Приложение Финансовый Менеджер готово к использованию."
echo ""
echo "Варианты запуска:"
echo "  - Для запуска через GUI:     ./run_finance_manager.sh"
echo "  - Для запуска через Maven:   mvn javafx:run"
echo "  - Для тестирования без GUI:  ./test_app.sh"
echo ""
echo "Документация:"
echo "  - docs/DEVELOPMENT.md - Руководство по разработке"
echo "  - docs/TESTING.md - Руководство по тестированию"
echo "  - docs/JAVAFX_PROBLEMS.md - Решение проблем с JavaFX"
echo "  - docs/REPLIT_TESTING.md - Тестирование в Replit"
echo ""
