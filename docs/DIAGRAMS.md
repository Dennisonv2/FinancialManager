# Диаграммы Финансового Менеджера

В этом документе представлены диаграммы, иллюстрирующие различные аспекты приложения "Финансовый Менеджер". Диаграммы представлены в формате PlantUML, который можно преобразовать в графические изображения с помощью онлайн-редакторов или плагинов для IDE.

## Диаграмма вариантов использования (Use Case Diagram)

```plantuml
@startuml
left to right direction
skinparam packageStyle rectangle

actor Пользователь as User

rectangle "Приложение Финансовый Менеджер" {
  usecase "Добавить доход" as UC1
  usecase "Добавить расход" as UC2
  usecase "Просмотреть транзакции" as UC3
  usecase "Фильтровать транзакции по категориям" as UC4
  usecase "Фильтровать транзакции по датам" as UC5
  usecase "Установить бюджет" as UC6
  usecase "Просмотреть бюджеты" as UC7
  usecase "Редактировать бюджет" as UC8
  usecase "Создать категорию" as UC9
  usecase "Просмотреть статистику" as UC10
  usecase "Управлять категориями" as UC11
}

User --> UC1
User --> UC2
User --> UC3
User --> UC6
User --> UC7
User --> UC9
User --> UC10
User --> UC11

UC3 <-- UC4 : extend
UC3 <-- UC5 : extend
UC7 <-- UC8 : extend
@enduml
```

### Описание вариантов использования

1. **Добавить доход** - Пользователь может добавить новый доход, указав категорию, сумму, дату и описание.
2. **Добавить расход** - Пользователь может добавить новый расход, указав категорию, сумму, дату и описание.
3. **Просмотреть транзакции** - Пользователь может просматривать список всех транзакций (доходов и расходов).
4. **Фильтровать транзакции по категориям** - Расширение варианта "Просмотреть транзакции", позволяет фильтровать транзакции по выбранным категориям.
5. **Фильтровать транзакции по датам** - Расширение варианта "Просмотреть транзакции", позволяет фильтровать транзакции по диапазону дат.
6. **Установить бюджет** - Пользователь может установить бюджет для определенной категории расходов.
7. **Просмотреть бюджеты** - Пользователь может просматривать установленные бюджеты и их текущее состояние.
8. **Редактировать бюджет** - Расширение варианта "Просмотреть бюджеты", позволяет изменить сумму существующего бюджета.
9. **Создать категорию** - Пользователь может создать новую категорию доходов или расходов.
10. **Просмотреть статистику** - Пользователь может просматривать статистику доходов и расходов.
11. **Управлять категориями** - Пользователь может просматривать, добавлять и редактировать категории.

## Диаграмма классов (Class Diagram)

```plantuml
@startuml
skinparam classAttributeIconSize 0

class Main {
  +main(args: String[]): void
}

class MainApp {
  +start(stage: Stage): void
  +main(args: String[]): void
}

class Controller {
  -view: View
  -dataAccess: DataAccess
  +initialize(): void
  +addTransaction(transaction: Transaction): void
  +getTransactions(): List<Transaction>
  +getCategories(type: String): List<Category>
  +addCategory(category: Category): void
  +setBudget(budget: Budget): void
  +getBudgets(): List<Budget>
}

class View {
  -controller: Controller
  -stage: Stage
  +initialize(stage: Stage): void
  +displayTransactions(transactions: List<Transaction>): void
  +displayCategories(categories: List<Category>): void
  +displayBudgets(budgets: List<Budget>): void
  +showAddTransactionDialog(): Transaction
  +showAddCategoryDialog(): Category
  +showAddBudgetDialog(): Budget
  +showError(message: String): void
  +showSuccess(message: String): void
}

class DataAccess {
  -connection: Connection
  +connect(): void
  +disconnect(): void
  +createTables(): void
  +addTransaction(transaction: Transaction): boolean
  +getTransactions(): List<Transaction>
  +addCategory(category: Category): boolean
  +getCategories(type: String): List<Category>
  +setCategoryBudget(categoryId: int, amount: double): boolean
  +getBudgets(): List<Budget>
  +updateBudget(budget: Budget): boolean
}

class Transaction {
  -id: int
  -date: LocalDate
  -type: String
  -categoryId: int
  -categoryName: String
  -amount: double
  -description: String
  +Transaction(date: LocalDate, type: String, categoryId: int, amount: double, description: String)
  +getId(): int
  +getDate(): LocalDate
  +getType(): String
  +getCategoryId(): int
  +getCategoryName(): String
  +getAmount(): double
  +getDescription(): String
  +setId(id: int): void
  +setCategoryName(name: String): void
}

class Category {
  -id: int
  -name: String
  -type: String
  +Category(name: String, type: String)
  +getId(): int
  +getName(): String
  +getType(): String
  +setId(id: int): void
}

class Budget {
  -id: int
  -categoryId: int
  -categoryName: String
  -amount: double
  -spent: double
  +Budget(categoryId: int, amount: double)
  +getId(): int
  +getCategoryId(): int
  +getCategoryName(): String
  +getAmount(): double
  +getSpent(): double
  +setId(id: int): void
  +setCategoryName(name: String): void
  +setSpent(spent: double): void
}

class TestApp {
  +main(args: String[]): void
  +testDBConnection(): boolean
  +testCreateTables(): void
  +testCategories(): void
  +testTransactions(): void
  +testBudgets(): void
}

Main --> MainApp
MainApp --> Controller
Controller --> View
Controller --> DataAccess
Controller --> Transaction
Controller --> Category
Controller --> Budget
TestApp --> DataAccess
TestApp --> Transaction
TestApp --> Category
TestApp --> Budget
@enduml
```

### Описание классов

1. **Main** - Основной класс приложения, точка входа.
2. **MainApp** - Класс, наследующий от `javafx.application.Application`, отвечает за инициализацию JavaFX.
3. **Controller** - Реализует логику взаимодействия между моделью данных и представлением (паттерн MVC).
4. **View** - Отвечает за графический интерфейс пользователя.
5. **DataAccess** - Обеспечивает взаимодействие с базой данных SQLite.
6. **Transaction** - Представляет отдельную транзакцию (доход или расход).
7. **Category** - Представляет категорию доходов или расходов.
8. **Budget** - Представляет бюджет для категории расходов.
9. **TestApp** - Класс для тестирования основных функций без GUI.

## Диаграмма последовательности (Sequence Diagram)

### Пример: Добавление новой транзакции

```plantuml
@startuml
actor Пользователь as User
participant "View" as View
participant "Controller" as Controller
participant "DataAccess" as DataAccess
participant "Transaction" as Transaction
database "SQLite" as DB

User -> View: Запрос на добавление транзакции
View -> View: showAddTransactionDialog()
User -> View: Ввод данных транзакции
View -> Transaction: create(date, type, categoryId, amount, description)
View -> Controller: addTransaction(transaction)
Controller -> DataAccess: addTransaction(transaction)
DataAccess -> DB: SQL INSERT
DB --> DataAccess: Результат операции
DataAccess --> Controller: Статус операции
Controller --> View: Статус операции
View -> View: Обновление UI
View --> User: Подтверждение добавления
@enduml
```

### Пример: Просмотр бюджетов

```plantuml
@startuml
actor Пользователь as User
participant "View" as View
participant "Controller" as Controller
participant "DataAccess" as DataAccess
database "SQLite" as DB

User -> View: Запрос на просмотр бюджетов
View -> Controller: getBudgets()
Controller -> DataAccess: getBudgets()
DataAccess -> DB: SQL SELECT
DB --> DataAccess: Результаты запроса
DataAccess --> Controller: Список бюджетов
Controller --> View: Список бюджетов
View -> View: displayBudgets(budgets)
View --> User: Отображение бюджетов
@enduml
```

## Компонентная диаграмма (Component Diagram)

```plantuml
@startuml
package "Финансовый Менеджер" {
  [GUI] as UI
  [Контроллер] as Controller
  [Модель данных] as Model
  [Доступ к БД] as DAL
  database "SQLite" as DB
  
  UI -down-> Controller : использует
  Controller -down-> Model : управляет
  Controller -down-> DAL : использует
  DAL -down-> DB : SQL-запросы
}

[Тестовое приложение] as TestApp
TestApp -right-> DAL : использует
TestApp -right-> Model : использует

[Внешняя система] as External

External -up-> [Финансовый Менеджер] : взаимодействует
@enduml
```

### Описание компонентов

1. **GUI (Пользовательский интерфейс)** - Компонент, отвечающий за отображение данных и взаимодействие с пользователем. Реализован с использованием JavaFX.
2. **Контроллер** - Компонент, реализующий бизнес-логику приложения и связывающий пользовательский интерфейс с моделью данных.
3. **Модель данных** - Компонент, содержащий классы для представления данных (Transaction, Category, Budget).
4. **Доступ к БД** - Компонент, обеспечивающий доступ к базе данных SQLite.
5. **SQLite** - База данных для хранения всех данных приложения.
6. **Тестовое приложение** - Отдельный компонент для тестирования функциональности без GUI.
7. **Внешняя система** - Любая внешняя система, которая может взаимодействовать с приложением (например, будущие расширения).

## Как использовать эти диаграммы

Вы можете визуализировать эти диаграммы с помощью:

1. Онлайн-редактора PlantUML: http://www.plantuml.com/plantuml/
2. Плагинов для различных IDE (IntelliJ IDEA, Visual Studio Code и др.)
3. Командной строки с установленным PlantUML

Просто скопируйте код диаграммы между тегами \`\`\`plantuml и \`\`\` в соответствующий инструмент.
