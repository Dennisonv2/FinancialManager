# Finance Manager Application

A Java desktop application for personal finance management with budget planning and expense tracking.

## Features

- Add and track income and expenses
- Categorize transactions
- Set and monitor budgets
- View financial summaries with colored transaction indicators
- Dark Mode UI for better usability
- Data stored in local SQLite database

## Running the Application

### GUI Version
```
mvn compile && mvn javafx:run
```

## Creating Executable Files

### Executable JAR

An executable JAR file has already been created in the `target/` directory:
```
FinanceManager-1.0-SNAPSHOT.jar
```

You can run this JAR on any system with Java installed:
```
java -jar FinanceManager-1.0-SNAPSHOT.jar
```

### Creating an EXE File (Windows)

To create an EXE file from the JAR, follow these steps:

1. **Using Launch4j (Recommended)**

   a. Download Launch4j from https://launch4j.sourceforge.net/
   
   b. Create a new configuration with the following settings:
      - Output file: `FinanceManager.exe`
      - Jar: Path to `FinanceManager-1.0-SNAPSHOT.jar`
      - JRE: Min version: 17.0.0
   
   c. Set the application details in the "Version Info" tab
   
   d. Click "Build wrapper"

2. **Alternative: Use an online converter**

   Several online services can convert JAR to EXE, such as:
   - jar2exe (https://www.jar2exe.com/)
   - Exe4j (https://www.ej-technologies.com/products/exe4j/overview.html)

### Creating a Distribution Package

For a complete distribution package, include the following files:
- The executable (.jar or .exe)
- A copy of the SQLite database (if you want to include sample data)
- README with instructions
- JRE folder (optional, for bundling Java with your application)

## Technical Details

- Language: Java 17
- Database: SQLite
- UI Framework: JavaFX with dark theme
- Architecture: MVC (Model-View-Controller)
- Build System: Maven

## Database Structure

- Categories (id, name, type)
- Transactions (id, date, type, category_id, amount, description)
- Budgets (id, category_id, amount)
