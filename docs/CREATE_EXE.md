# Создание .exe файла для Finance Manager

Для создания исполняемого .exe файла для Windows, выполните следующие шаги:

## Предварительные требования

1. Java Development Kit (JDK) версии 17 или выше
2. Maven 3.8.0 или выше
3. Инструмент Launch4j (http://launch4j.sourceforge.net/)

## Шаг 1: Компиляция проекта

```bash
mvn clean package
```

Эта команда создаст два JAR-файла в папке `target`:
- `FinanceManager-1.0-SNAPSHOT.jar` (создан плагином maven-assembly-plugin)
- `FinanceManager-executable-1.0-SNAPSHOT.jar` (создан плагином maven-shade-plugin)

## Шаг 2: Создание .exe файла с помощью Launch4j

### Использование Maven плагина (рекомендуется)

В проекте уже настроен плагин Launch4j. После выполнения `mvn clean package`, .exe файл автоматически создается в папке `target` с именем `FinanceManager.exe`. 

Если файл не создается автоматически, то это может быть связано с отсутствием плагина Launch4j или его неправильной настройкой. В этом случае используйте графический интерфейс Launch4j (следующий раздел).

### Использование графического интерфейса Launch4j

1. Скачайте и установите Launch4j с официального сайта: http://launch4j.sourceforge.net/
2. Запустите Launch4j
3. Заполните следующие поля:
   - **Basic**:
     - Output file: путь к файлу .exe (например, `C:\path\to\FinanceManager.exe`)
     - Jar: путь к JAR-файлу (например, `C:\path\to\FinanceManager-executable-1.0-SNAPSHOT.jar`)
     - Icon: можно добавить значок .ico для вашего приложения (опционально)
   - **JRE**:
     - Bundled JRE path: оставьте пустым (если вы не хотите включать JRE в ваш установщик)
     - Min JRE version: 17.0.0
   - **Classpath**:
     - Main class: `com.financemanager.Main`
   - **Header**:
     - Header type: GUI
   - **Version Info** (опционально):
     - File version: 1.0.0.0
     - Product version: 1.0.0.0
     - File description: Personal Finance Manager Application
     - Product name: Finance Manager

4. Сохраните конфигурацию и нажмите на кнопку "Build wrapper"

## Шаг 3: Распространение

При распространении приложения необходимо убедиться, что:

1. База данных SQLite доступна вместе с приложением
2. На компьютере пользователя установлена Java 17 или выше
3. Все необходимые ресурсы включены в JAR-файл

## Дополнительные опции

### Создание установщика

Если вы хотите создать полноценный установщик для Windows, вы можете использовать инструменты вроде:
- Inno Setup (https://jrsoftware.org/isinfo.php)
- NSIS (https://nsis.sourceforge.io/Main_Page)
- WiX Toolset (https://wixtoolset.org/)

### Полный автономный пакет (с JRE)

Для создания полностью автономного приложения, включающего JRE, вы можете использовать инструмент jpackage, входящий в состав JDK 14+:

```bash
jpackage --input target/ --main-jar FinanceManager-executable-1.0-SNAPSHOT.jar --main-class com.financemanager.Main --name FinanceManager --app-version 1.0 --vendor "Your Name" --win-menu --win-shortcut
```

Этот инструмент создаст установщик Windows (.msi или .exe), который включает JRE и все необходимые зависимости.
