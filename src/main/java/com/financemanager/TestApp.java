package com.financemanager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Тестовый класс для проверки функциональности без JavaFX
 * Этот класс можно использовать для тестирования основных компонентов приложения
 * в среде без графического интерфейса (например, Replit)
 */
public class TestApp {
    
    public static void main(String[] args) {
        System.out.println("===== Тестирование компонентов приложения Финансовый Менеджер =====");
        
        // Инициализация доступа к данным
        DataAccess dataAccess = new DataAccess();
        dataAccess.connect();
        
        try {
            // Создание таблиц, если они не существуют
            System.out.println("\n>> Создание таблиц базы данных");
            dataAccess.createTablesIfNotExist();
            
            // Проверка категорий
            testCategories(dataAccess);
            
            // Проверка транзакций
            testTransactions(dataAccess);
            
            // Проверка бюджетов
            testBudgets(dataAccess);
            
            System.out.println("\n===== Все тесты успешно пройдены! =====");
            
        } catch (Exception e) {
            System.err.println("Ошибка при тестировании: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Закрытие соединения с базой данных
            dataAccess.disconnect();
        }
    }
    
    /**
     * Тестирование функциональности категорий
     */
    private static void testCategories(DataAccess dataAccess) {
        System.out.println("\n>> Тестирование категорий");
        
        // Получение всех категорий
        List<Category> categories = dataAccess.getAllCategories();
        System.out.println("Всего категорий: " + categories.size());
        
        // Создание категорий, если список пуст
        if (categories.isEmpty()) {
            System.out.println("Создание стандартных категорий...");
            
            // Стандартные категории доходов
            dataAccess.addCategory(new Category(0, "Зарплата", "Income"));
            dataAccess.addCategory(new Category(0, "Премия", "Income"));
            dataAccess.addCategory(new Category(0, "Инвестиции", "Income"));
            dataAccess.addCategory(new Category(0, "Подарок", "Income"));
            dataAccess.addCategory(new Category(0, "Прочие доходы", "Income"));
            
            // Стандартные категории расходов
            dataAccess.addCategory(new Category(0, "Продукты", "Expense"));
            dataAccess.addCategory(new Category(0, "Жилье", "Expense"));
            dataAccess.addCategory(new Category(0, "Транспорт", "Expense"));
            dataAccess.addCategory(new Category(0, "Развлечения", "Expense"));
            dataAccess.addCategory(new Category(0, "Коммунальные услуги", "Expense"));
            dataAccess.addCategory(new Category(0, "Здоровье", "Expense"));
            dataAccess.addCategory(new Category(0, "Образование", "Expense"));
            dataAccess.addCategory(new Category(0, "Рестораны", "Expense"));
            dataAccess.addCategory(new Category(0, "Одежда", "Expense"));
            dataAccess.addCategory(new Category(0, "Техника", "Expense"));
            dataAccess.addCategory(new Category(0, "Путешествия", "Expense"));
            dataAccess.addCategory(new Category(0, "Подарки", "Expense"));
            dataAccess.addCategory(new Category(0, "Прочие расходы", "Expense"));
            
            // Получение обновленного списка категорий
            categories = dataAccess.getAllCategories();
            System.out.println("Создано категорий: " + categories.size());
        }
        
        // Вывод всех категорий с русскими названиями типов
        for (Category category : categories) {
            String typeDisplay = "Income".equals(category.getType()) ? "Доход" : "Расход";
            System.out.println(typeDisplay + ": " + category.getName() + " (ID: " + category.getId() + ")");
        }
    }
    
    /**
     * Тестирование функциональности транзакций
     */
    private static void testTransactions(DataAccess dataAccess) {
        System.out.println("\n>> Тестирование транзакций");
        
        // Получение всех транзакций
        List<Transaction> transactions = dataAccess.getAllTransactions();
        System.out.println("Всего транзакций: " + transactions.size());
        
        // Получение всех категорий
        List<Category> categories = dataAccess.getAllCategories();
        
        if (!categories.isEmpty()) {
            // Поиск категории дохода
            Category incomeCategory = categories.stream()
                    .filter(c -> "Income".equals(c.getType()))
                    .findFirst()
                    .orElse(null);
                    
            // Поиск категории расхода
            Category expenseCategory = categories.stream()
                    .filter(c -> "Expense".equals(c.getType()))
                    .findFirst()
                    .orElse(null);
            
            // Создание тестовых транзакций, если нет существующих
            if (transactions.isEmpty() && incomeCategory != null && expenseCategory != null) {
                System.out.println("Создание тестовых транзакций...");
                
                // Тестовая транзакция доходов
                Transaction income = new Transaction(
                    0,
                    LocalDate.now(),
                    "Income",
                    incomeCategory.getId(),
                    incomeCategory.getName(),
                    new BigDecimal("50000.00"),
                    "Тестовый доход"
                );
                dataAccess.addTransaction(income);
                
                // Тестовая транзакция расходов
                Transaction expense = new Transaction(
                    0,
                    LocalDate.now(),
                    "Expense",
                    expenseCategory.getId(),
                    expenseCategory.getName(),
                    new BigDecimal("15000.00"),
                    "Тестовый расход"
                );
                dataAccess.addTransaction(expense);
                
                // Получение обновленного списка транзакций
                transactions = dataAccess.getAllTransactions();
                System.out.println("Создано транзакций: " + transactions.size());
            }
        }
        
        // Вывод всех транзакций с русскими обозначениями типов
        for (Transaction transaction : transactions) {
            String typeDisplay = "Income".equals(transaction.getType()) ? "Доход" : "Расход";
            System.out.println(
                transaction.getDate() + " | " + 
                typeDisplay + " | " + 
                transaction.getCategoryName() + " | " + 
                transaction.getAmount() + " ₽ | " + 
                transaction.getDescription()
            );
        }
    }
    
    /**
     * Тестирование функциональности бюджетов
     */
    private static void testBudgets(DataAccess dataAccess) {
        System.out.println("\n>> Тестирование бюджетов");
        
        // Получение всех бюджетов
        List<Budget> budgets = dataAccess.getAllBudgets();
        System.out.println("Всего бюджетов: " + budgets.size());
        
        // Получение категорий расходов
        List<Category> expenseCategories = dataAccess.getAllCategories().stream()
                .filter(c -> "Expense".equals(c.getType()))
                .toList();
        
        // Создание тестовых бюджетов для категорий расходов, если нет существующих
        if (budgets.isEmpty() && !expenseCategories.isEmpty()) {
            System.out.println("Создание тестовых бюджетов...");
            
            for (Category category : expenseCategories) {
                // Создание бюджета для категории расходов
                Budget budget = new Budget(
                    0,
                    category.getId(),
                    category.getName(),
                    new BigDecimal("10000.00") // 10,000 ₽ для каждой категории
                );
                dataAccess.addBudget(budget);
            }
            
            // Получение обновленного списка бюджетов
            budgets = dataAccess.getAllBudgets();
            System.out.println("Создано бюджетов: " + budgets.size());
        }
        
        // Вывод всех бюджетов
        for (Budget budget : budgets) {
            // Получение суммы расходов для категории
            BigDecimal spent = dataAccess.getExpenseAmountForCategory(budget.getCategoryId());
            
            System.out.println(
                budget.getCategoryName() + " | " + 
                "Бюджет: " + budget.getAmount() + " ₽ | " + 
                "Потрачено: " + spent + " ₽ | " + 
                "Осталось: " + budget.getAmount().subtract(spent) + " ₽"
            );
        }
    }
}
