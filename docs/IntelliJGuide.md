# Запуск Finance Manager в IntelliJ IDEA

## Решение проблемы "JavaFX runtime components are missing"

Если при запуске приложения вы видите ошибку "JavaFX runtime components are missing", вот несколько способов её решить:

### Способ 1: Запуск через Maven (рекомендуется)

Это самый простой и надёжный способ:

1. Откройте вкладку Maven в IntelliJ IDEA (обычно справа)
2. Разверните проект -> Plugins -> javafx
3. Дважды кликните на "javafx:run"

Это запустит приложение с правильно настроенными модулями JavaFX.

### Способ 2: Запуск через MainApp

Мы создали специальный класс-обертку для более легкого запуска:

1. Найдите файл `src/main/java/com/financemanager/MainApp.java`
2. Кликните правой кнопкой мыши -> Run 'MainApp.main()'

Это должно запустить приложение без дополнительных настроек.

### Способ 3: Настройка VM-параметров для Main класса

Если вы хотите запускать оригинальный Main класс:

1. Перейдите в Run -> Edit Configurations
2. Найдите или создайте конфигурацию для Main класса
3. В поле "VM options" добавьте следующую строку:
   ```
   --module-path "ПУТЬ_К_JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml
   ```
   
   где "ПУТЬ_К_JAVAFX_LIB" - это путь к библиотекам JavaFX на вашем компьютере.

   Например, в Windows это может выглядеть так:
   ```
   --module-path "C:\Java\javafx-sdk-17.0.2\lib" --add-modules javafx.controls,javafx.fxml
   ```

   В macOS или Linux:
   ```
   --module-path "/path/to/javafx-sdk-17.0.2/lib" --add-modules javafx.controls,javafx.fxml
   ```

### Способ 4: Создание глобальной переменной PATH_TO_FX

1. Перейдите в File -> Settings -> Appearance & Behavior -> Path Variables
2. Добавьте новую переменную с именем `PATH_TO_FX`
3. Укажите путь к папке lib в вашей JavaFX SDK
4. Затем в VM options используйте:
   ```
   --module-path "${PATH_TO_FX}" --add-modules javafx.controls,javafx.fxml
   ```

### Способ 5: Консольная версия (без JavaFX)

Если вам нужна только функциональность без графического интерфейса:

1. Запустите класс `com.financemanager.ConsoleMain`
2. Работайте с приложением через консольный интерфейс

## Дополнительные советы

1. **Установка JavaFX SDK**:
   - Скачайте JavaFX SDK с [сайта OpenJFX](https://openjfx.io/)
   - Распакуйте в удобное место на вашем компьютере

2. **Проверка Maven-зависимостей**:
   - Убедитесь, что Maven правильно загрузил все зависимости
   - Иногда помогает команда: `mvn clean install -U`

3. **Проверка версии Java**:
   - Для этого проекта требуется Java 17 или выше
   - Проверьте версию в IntelliJ: File -> Project Structure -> Project
