package com.financemanager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console version of the Finance Manager application
 */
public class ConsoleMain {
    private static final Scanner scanner = new Scanner(System.in);
    private static DataAccess dataAccess;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("==== Personal Finance Manager ====");
        
        // Initialize database
        dataAccess = new DataAccess();
        dataAccess.connect();
        dataAccess.createTablesIfNotExist();
        
        // Initialize default categories if needed
        if (dataAccess.getAllCategories().isEmpty()) {
            initializeDefaultCategories();
        }
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1: // View Transactions
                    viewTransactions();
                    break;
                case 2: // Add Income
                    addTransaction("Income");
                    break;
                case 3: // Add Expense
                    addTransaction("Expense");
                    break;
                case 4: // View Budget
                    viewBudget();
                    break;
                case 5: // Set Budget
                    setBudget();
                    break;
                case 6: // View Summary
                    viewSummary();
                    break;
                case 0: // Exit
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
        
        // Clean up
        dataAccess.disconnect();
        System.out.println("Thank you for using Personal Finance Manager!");
    }
    
    /**
     * Displays the main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. View Transactions");
        System.out.println("2. Add Income");
        System.out.println("3. Add Expense");
        System.out.println("4. View Budget");
        System.out.println("5. Set Budget");
        System.out.println("6. View Summary");
        System.out.println("0. Exit");
        System.out.println("====================");
    }
    
    /**
     * Displays all transactions in a tabular format
     */
    private static void viewTransactions() {
        List<Transaction> transactions = dataAccess.getAllTransactions();
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        System.out.println("\n===== TRANSACTIONS =====");
        System.out.printf("%-5s | %-10s | %-8s | %-15s | %-10s | %s%n", 
                "ID", "DATE", "TYPE", "CATEGORY", "AMOUNT", "DESCRIPTION");
        System.out.println("------------------------------------------------------");
        
        for (Transaction t : transactions) {
            System.out.printf("%-5d | %-10s | %-8s | %-15s | $%-9.2f | %s%n", 
                    t.getId(), 
                    t.getDate().format(dateFormatter),
                    t.getType(),
                    t.getCategoryName(),
                    t.getAmount(),
                    t.getDescription());
        }
    }
    
    /**
     * Adds a new transaction (income or expense)
     * @param type The type of transaction to add
     */
    private static void addTransaction(String type) {
        System.out.println("\n===== ADD " + type.toUpperCase() + " =====");
        
        // Get date
        LocalDate date = getDateInput("Enter date (YYYY-MM-DD) or leave blank for today: ");
        if (date == null) {
            date = LocalDate.now();
        }
        
        // Get category
        List<Category> categories = dataAccess.getAllCategories();
        List<Category> filteredCategories = categories.stream()
                .filter(c -> c.getType().equals(type))
                .toList();
        
        if (filteredCategories.isEmpty()) {
            System.out.println("No categories found for " + type + ".");
            return;
        }
        
        System.out.println("Available categories:");
        for (int i = 0; i < filteredCategories.size(); i++) {
            System.out.println((i + 1) + ". " + filteredCategories.get(i).getName());
        }
        
        int categoryIndex = getIntInput("Select category number: ") - 1;
        if (categoryIndex < 0 || categoryIndex >= filteredCategories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }
        
        Category selectedCategory = filteredCategories.get(categoryIndex);
        
        // Get amount
        BigDecimal amount = getBigDecimalInput("Enter amount: $");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            return;
        }
        
        // Get description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        
        // Create and save transaction
        Transaction transaction = new Transaction();
        transaction.setDate(date);
        transaction.setType(type);
        transaction.setCategoryId(selectedCategory.getId());
        transaction.setCategoryName(selectedCategory.getName());
        transaction.setAmount(amount);
        transaction.setDescription(description);
        
        boolean success = dataAccess.addTransaction(transaction);
        if (success) {
            System.out.println(type + " added successfully!");
        } else {
            System.out.println("Failed to add " + type + ".");
        }
    }
    
    /**
     * Displays all budgets and their progress
     */
    private static void viewBudget() {
        List<Budget> budgets = dataAccess.getAllBudgets();
        
        if (budgets.isEmpty()) {
            System.out.println("No budgets found. Use 'Set Budget' to create one.");
            return;
        }
        
        System.out.println("\n===== BUDGET =====");
        System.out.printf("%-20s | %-12s | %-12s | %-10s%n", 
                "CATEGORY", "BUDGET", "SPENT", "PROGRESS");
        System.out.println("--------------------------------------------------");
        
        for (Budget b : budgets) {
            // Calculate spent amount
            BigDecimal spent = dataAccess.getExpenseAmountForCategory(b.getCategoryId());
            b.setSpent(spent);
            
            // Calculate progress percentage
            double progress = b.getProgress();
            
            // Display budget information
            System.out.printf("%-20s | $%-11.2f | $%-11.2f | %.1f%%%n", 
                    b.getCategoryName(),
                    b.getAmount(),
                    b.getSpent(),
                    progress);
        }
    }
    
    /**
     * Allows setting a budget for an expense category
     */
    private static void setBudget() {
        System.out.println("\n===== SET BUDGET =====");
        
        // Get expense categories
        List<Category> categories = dataAccess.getAllCategories();
        List<Category> expenseCategories = categories.stream()
                .filter(c -> c.getType().equals("Expense"))
                .toList();
        
        if (expenseCategories.isEmpty()) {
            System.out.println("No expense categories found.");
            return;
        }
        
        System.out.println("Available expense categories:");
        for (int i = 0; i < expenseCategories.size(); i++) {
            System.out.println((i + 1) + ". " + expenseCategories.get(i).getName());
        }
        
        int categoryIndex = getIntInput("Select category number: ") - 1;
        if (categoryIndex < 0 || categoryIndex >= expenseCategories.size()) {
            System.out.println("Invalid category selection.");
            return;
        }
        
        Category selectedCategory = expenseCategories.get(categoryIndex);
        
        // Get budget amount
        BigDecimal amount = getBigDecimalInput("Enter budget amount: $");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Budget amount must be greater than zero.");
            return;
        }
        
        // Check if budget already exists for this category
        List<Budget> budgets = dataAccess.getAllBudgets();
        Budget existingBudget = null;
        
        for (Budget b : budgets) {
            if (b.getCategoryId() == selectedCategory.getId()) {
                existingBudget = b;
                break;
            }
        }
        
        boolean success;
        if (existingBudget != null) {
            // Update existing budget
            existingBudget.setAmount(amount);
            success = dataAccess.updateBudget(existingBudget);
        } else {
            // Create new budget
            Budget newBudget = new Budget();
            newBudget.setCategoryId(selectedCategory.getId());
            newBudget.setCategoryName(selectedCategory.getName());
            newBudget.setAmount(amount);
            success = dataAccess.addBudget(newBudget);
        }
        
        if (success) {
            System.out.println("Budget set successfully!");
        } else {
            System.out.println("Failed to set budget.");
        }
    }
    
    /**
     * Displays a summary of income, expenses, and balance
     */
    private static void viewSummary() {
        List<Transaction> transactions = dataAccess.getAllTransactions();
        
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction t : transactions) {
            if ("Income".equals(t.getType())) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if ("Expense".equals(t.getType())) {
                totalExpense = totalExpense.add(t.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        System.out.println("\n===== FINANCIAL SUMMARY =====");
        System.out.printf("Total Income:  $%.2f%n", totalIncome);
        System.out.printf("Total Expense: $%.2f%n", totalExpense);
        System.out.printf("Balance:       $%.2f%n", balance);
        
        // Display category breakdown
        System.out.println("\n----- CATEGORY BREAKDOWN -----");
        List<Category> categories = dataAccess.getAllCategories();
        
        System.out.println("\nINCOME CATEGORIES:");
        for (Category category : categories) {
            if ("Income".equals(category.getType())) {
                BigDecimal total = BigDecimal.ZERO;
                for (Transaction t : transactions) {
                    if (t.getCategoryId() == category.getId()) {
                        total = total.add(t.getAmount());
                    }
                }
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.printf("  %-15s: $%.2f%n", category.getName(), total);
                }
            }
        }
        
        System.out.println("\nEXPENSE CATEGORIES:");
        for (Category category : categories) {
            if ("Expense".equals(category.getType())) {
                BigDecimal total = BigDecimal.ZERO;
                for (Transaction t : transactions) {
                    if (t.getCategoryId() == category.getId()) {
                        total = total.add(t.getAmount());
                    }
                }
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.printf("  %-15s: $%.2f%n", category.getName(), total);
                }
            }
        }
    }
    
    /**
     * Creates default categories if none exist
     */
    private static void initializeDefaultCategories() {
        System.out.println("Initializing default categories...");
        
        // Create default income categories
        dataAccess.addCategory(new Category("Salary", "Income"));
        dataAccess.addCategory(new Category("Bonus", "Income"));
        dataAccess.addCategory(new Category("Investment", "Income"));
        dataAccess.addCategory(new Category("Gift", "Income"));
        
        // Create default expense categories
        dataAccess.addCategory(new Category("Food", "Expense"));
        dataAccess.addCategory(new Category("Housing", "Expense"));
        dataAccess.addCategory(new Category("Transportation", "Expense"));
        dataAccess.addCategory(new Category("Entertainment", "Expense"));
        dataAccess.addCategory(new Category("Utilities", "Expense"));
        dataAccess.addCategory(new Category("Healthcare", "Expense"));
        dataAccess.addCategory(new Category("Education", "Expense"));
        dataAccess.addCategory(new Category("Other", "Expense"));
    }
    
    /**
     * Gets integer input from the user with validation
     * @param prompt The prompt to display
     * @return The integer entered by the user
     */
    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets BigDecimal input from the user with validation
     * @param prompt The prompt to display
     * @return The BigDecimal entered by the user
     */
    private static BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    /**
     * Gets date input from the user with validation
     * @param prompt The prompt to display
     * @return The LocalDate entered by the user, or null if empty
     */
    private static LocalDate getDateInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        
        if (input.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Using today's date.");
            return LocalDate.now();
        }
    }
}
