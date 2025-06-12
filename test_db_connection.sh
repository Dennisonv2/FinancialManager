#!/bin/bash

echo "===== Тестирование подключения к базе данных ====="

# Создание простого класса для тестирования SQLite
cat > TestSQLite.java << 'EOT'
import java.sql.*;

public class TestSQLite {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Загрузка драйвера SQLite
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite драйвер успешно загружен!");
            
            // Создание соединения с базой данных
            connection = DriverManager.getConnection("jdbc:sqlite:finance_manager.db");
            System.out.println("Подключение к базе данных успешно установлено!");
            
            // Создание тестовой таблицы
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT)");
            System.out.println("Тестовая таблица создана!");
            
            // Закрытие соединения
            connection.close();
            System.out.println("Соединение с базой данных закрыто.");
            
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
EOT

# Компиляция и запуск теста
echo "Компиляция тестового класса..."
javac -cp "sqlite-jdbc-3.36.0.3.jar" TestSQLite.java

echo "Запуск теста базы данных..."
java -cp ".:sqlite-jdbc-3.36.0.3.jar" TestSQLite

# Проверка результата теста
if [ $? -ne 0 ]; then
    echo "Тест не пройден. Проверьте настройки базы данных."
    exit 1
fi

echo "===== Тест подключения к базе данных пройден успешно! ====="
echo "База данных SQLite успешно настроена и готова к использованию."

# Очистка временных файлов
rm TestSQLite.java
rm TestSQLite.class

