#!/bin/bash

echo "===== Запуск тестового приложения Финансовый Менеджер ====="

# Проверка доступности Maven
if ! command -v mvn &> /dev/null; then
    echo "Maven не найден. Используем скомпилированные классы."
    CLASSPATH="target/classes:sqlite-jdbc-3.36.0.3.jar"
else
    echo "Компиляция проекта с помощью Maven..."
    mvn compile
    CLASSPATH="target/classes:sqlite-jdbc-3.36.0.3.jar"
fi

# Запуск тестового приложения
echo "Запуск тестового приложения..."
java -cp $CLASSPATH com.financemanager.TestApp

# Проверка результата теста
if [ $? -ne 0 ]; then
    echo "Тест не пройден. Проверьте ошибки, указанные выше."
    exit 1
fi

echo "===== Тестирование завершено успешно! ====="
