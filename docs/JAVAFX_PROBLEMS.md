# Решение проблем с JavaFX

## Распространённые ошибки JavaFX

При работе с JavaFX в приложении "Финансовый Менеджер" могут возникать следующие ошибки:

### 1. "JavaFX runtime components are missing"

**Симптомы:**
```
Error: JavaFX runtime components are missing, and are required to run this application
```

**Причина:** Java не может найти необходимые компоненты JavaFX, поскольку они не входят в стандартную поставку JDK с Java 11.

**Решения:**
- Запуск через Maven: `mvn javafx:run`
- Использование класса `MainApp.java` вместо `Main.java`
- Добавление параметров VM:
  ```
  --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
  ```

### 2. "Unsupported JavaFX configuration"

**Симптомы:**
```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @...'
```

**Причина:** Проблема модульной системы Java, связанная с загрузкой классов JavaFX.

**Решения:**
- Добавить опции в конфигурацию Maven:
  ```xml
  <options>
      <option>--add-opens</option>
      <option>java.base/java.lang=ALL-UNNAMED</option>
  </options>
  ```
- Использовать предварительно настроенный скрипт запуска: `./run_finance_manager.sh`

### 3. Ошибки в безголовых средах (Headless Environments)

**Симптомы:**
```
java.lang.UnsatisfiedLinkError: ... libprism_es2.so: libGL.so.1: cannot open shared object file
```
или
```
Loading library prism_es2 from resource failed: java.lang.UnsatisfiedLinkError: /tmp/.../libprism_es2.so: Error loading shared library...
```

**Причина:** JavaFX требует графический интерфейс, которого нет в безголовых средах (например, в Replit или на серверах).

**Решения:**
- Для тестирования в Replit используйте скрипты:
  - `./test_db_connection.sh` - проверка подключения к базе данных
  - `./test_app.sh` - тестирование функциональности без GUI
- Для серверов и CI/CD сред создайте отдельную версию без JavaFX

## NullPointerException в Controller.java

**Симптомы:**
```
Caused by: java.lang.NullPointerException: Cannot invoke "com.financemanager.DataAccess.getAllCategories()" because "this.dataAccess" is null
```

**Причина:** Объект DataAccess не инициализирован перед вызовом метода.

**Решение:**
Убедитесь, что в классе Controller.java объект dataAccess инициализирован:

```java
private DataAccess dataAccess;

@FXML
public void initialize() {
    // Инициализация объекта dataAccess
    dataAccess = new DataAccess();
    dataAccess.connect();
    
    // Остальной код инициализации
    loadCategories();
    loadTransactions();
    // ...
}
```

## Заключение

JavaFX - мощная библиотека для создания GUI, но она имеет ряд особенностей, которые могут вызвать проблемы в некоторых средах. Если вы испытываете проблемы с запуском приложения:

1. В среде разработки (IntelliJ IDEA, Eclipse):
   - Используйте класс `MainApp.java` для запуска
   - Настройте VM параметры в конфигурации запуска

2. На обычном компьютере:
   - Используйте скрипты запуска: `run_finance_manager.sh` или `run_finance_manager.bat`
   - Убедитесь, что Java 17+ установлена

3. В безголовых средах:
   - Используйте тестовые скрипты без GUI
   - Рассмотрите возможность создания консольной версии приложения
