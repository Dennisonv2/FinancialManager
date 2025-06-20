# Руководство по тестированию

В этом документе описаны различные подходы к тестированию приложения "Финансовый Менеджер" в разных средах.

## Тестирование с GUI (на локальном компьютере)

### Предварительные требования
- Java 17 или выше
- JavaFX SDK (включен в зависимости Maven)
- Интегрированная среда разработки (IntelliJ IDEA, Eclipse) или командная строка

### Запуск через скрипты

Самый простой способ запустить приложение для тестирования:

- **Linux/macOS**: `./run_finance_manager.sh`
- **Windows**: `run_finance_manager.bat`

Эти скрипты содержат все необходимые параметры для корректного запуска JavaFX.

### Запуск через IDE

1. **IntelliJ IDEA**
   - Откройте проект
   - Запустите класс `MainApp.java` (не `Main.java`)
   - Если возникает ошибка JavaFX, добавьте VM параметры:
     ```
     --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
     ```

2. **Eclipse**
   - Откройте проект
   - Создайте конфигурацию запуска для `MainApp.java`
   - В Arguments → VM arguments добавьте:
     ```
     --module-path ${pathToJavaFX} --add-modules javafx.controls,javafx.fxml
     ```
   - Замените `${pathToJavaFX}` на путь к директории lib в JavaFX SDK

### Запуск через Maven
```
mvn clean javafx:run
```

### Сценарии тестирования (с GUI)

1. **Создание транзакций**
   - Добавьте новый доход (категория "Зарплата", сумма 50,000₽)
   - Добавьте новый расход (категория "Продукты", сумма 5,000₽)
   - Проверьте, что транзакции отображаются в списке
   - Убедитесь, что доходы отображаются зеленым, а расходы - красным

2. **Управление бюджетами**
   - Создайте новый бюджет для категории "Развлечения"
   - Добавьте расход в эту категорию и проверьте обновление оставшейся суммы бюджета
   - Измените сумму бюджета и проверьте обновление данных

3. **Фильтрация и поиск**
   - Отфильтруйте транзакции по категории
   - Отфильтруйте транзакции по типу (доход/расход)
   - Отфильтруйте транзакции по датам (за месяц, за неделю)
   - Выполните поиск транзакций по описанию

## Тестирование без GUI (Replit, CI/CD, серверы)

В средах без поддержки графического интерфейса тестирование выполняется с помощью специальных скриптов.

### Тестирование подключения к базе данных
```
./test_db_connection.sh
```

Этот скрипт проверяет:
- Подключение к базе данных SQLite
- Создание тестовой таблицы
- Выполнение базовых SQL-запросов

### Тестирование бизнес-логики
```
./test_app.sh
```

Этот скрипт запускает класс `TestApp.java`, который тестирует:
- Создание таблиц в базе данных
- Работу с категориями доходов и расходов
- Создание и получение транзакций
- Управление бюджетами

### Тестирование в CI/CD пайплайне

Для интеграции с CI/CD системами (GitHub Actions, Jenkins, GitLab CI) используйте скрипт `test_finance_manager.sh`, который:

1. Компилирует проект с помощью Maven
2. Выполняет тесты в безголовом режиме
3. Возвращает код завершения 0 в случае успеха, ненулевой код в случае ошибки

Пример использования в GitHub Actions:
```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: 17
          distribution: 'adopt'
      - name: Run tests
        run: ./test_finance_manager.sh
```

## Отладка проблем с JavaFX

Если вы столкнулись с проблемами при запуске JavaFX, обратитесь к документу [JAVAFX_PROBLEMS.md](JAVAFX_PROBLEMS.md) для подробного описания распространенных ошибок и способов их решения.

## Проверка созданного JAR-файла

Чтобы убедиться, что собранный JAR-файл работает корректно:

1. Соберите JAR с помощью Maven:
   ```
   mvn clean package
   ```

2. Запустите JAR-файл:
   ```
   java -jar target/FinanceManager-1.0-SNAPSHOT.jar
   ```

   Если возникает ошибка JavaFX, используйте:
   ```
   java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/FinanceManager-1.0-SNAPSHOT.jar
   ```

3. Проверьте, что все функции (добавление транзакций, управление бюджетами и т.д.) работают как ожидается.
