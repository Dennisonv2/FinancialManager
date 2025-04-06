package com.financemanager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class for database operations
 */
public class DataAccess {
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:finance_manager.db";
    
    /**
     * Establishes a connection to the SQLite database
     */
    public void connect() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Closes the database connection
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates database tables if they don't exist
     */
    public void createTablesIfNotExist() {
        try {
            Statement statement = connection.createStatement();
            
            // Create categories table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL" +
                ")"
            );
            
            // Create transactions table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "description TEXT, " +
                "FOREIGN KEY (category_id) REFERENCES categories (id)" +
                ")"
            );
            
            // Create budgets table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "category_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "FOREIGN KEY (category_id) REFERENCES categories (id)" +
                ")"
            );
            
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves all categories from the database
     * @return List of Category objects
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM categories ORDER BY type, name");
            
            while (resultSet.next()) {
                Category category = new Category(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("type")
                );
                categories.add(category);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving categories: " + e.getMessage());
            e.printStackTrace();
        }
        
        return categories;
    }
    
    /**
     * Adds a new category to the database
     * @param category The category to add
     * @return true if successful, false otherwise
     */
    public boolean addCategory(Category category) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO categories (name, type) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            statement.setString(1, category.getName());
            statement.setString(2, category.getType());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getLong(1));
                }
                generatedKeys.close();
            }
            
            statement.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all transactions from the database
     * @return List of Transaction objects
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT t.*, c.name as category_name " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "ORDER BY t.date DESC"
            );
            
            while (resultSet.next()) {
                Transaction transaction = new Transaction(
                    resultSet.getLong("id"),
                    LocalDate.parse(resultSet.getString("date")),
                    resultSet.getString("type"),
                    resultSet.getLong("category_id"),
                    resultSet.getString("category_name"),
                    BigDecimal.valueOf(resultSet.getDouble("amount")),
                    resultSet.getString("description")
                );
                transactions.add(transaction);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Adds a new transaction to the database
     * @param transaction The transaction to add
     * @return true if successful, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO transactions (date, type, category_id, amount, description) " +
                "VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            statement.setString(1, transaction.getDate().toString());
            statement.setString(2, transaction.getType());
            statement.setLong(3, transaction.getCategoryId());
            statement.setDouble(4, transaction.getAmount().doubleValue());
            statement.setString(5, transaction.getDescription());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getLong(1));
                }
                generatedKeys.close();
            }
            
            statement.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing transaction in the database
     * @param transaction The transaction to update
     * @return true if successful, false otherwise
     */
    public boolean updateTransaction(Transaction transaction) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE transactions SET date = ?, type = ?, category_id = ?, " +
                "amount = ?, description = ? WHERE id = ?"
            );
            
            statement.setString(1, transaction.getDate().toString());
            statement.setString(2, transaction.getType());
            statement.setLong(3, transaction.getCategoryId());
            statement.setDouble(4, transaction.getAmount().doubleValue());
            statement.setString(5, transaction.getDescription());
            statement.setLong(6, transaction.getId());
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a transaction from the database
     * @param transactionId The ID of the transaction to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteTransaction(long transactionId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM transactions WHERE id = ?"
            );
            
            statement.setLong(1, transactionId);
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all budgets from the database
     * @return List of Budget objects
     */
    public List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT b.*, c.name as category_name " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.id " +
                "ORDER BY c.name"
            );
            
            while (resultSet.next()) {
                Budget budget = new Budget(
                    resultSet.getLong("id"),
                    resultSet.getLong("category_id"),
                    resultSet.getString("category_name"),
                    BigDecimal.valueOf(resultSet.getDouble("amount"))
                );
                budgets.add(budget);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving budgets: " + e.getMessage());
            e.printStackTrace();
        }
        
        return budgets;
    }
    
    /**
     * Adds a new budget to the database
     * @param budget The budget to add
     * @return true if successful, false otherwise
     */
    public boolean addBudget(Budget budget) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO budgets (category_id, amount) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            
            statement.setLong(1, budget.getCategoryId());
            statement.setDouble(2, budget.getAmount().doubleValue());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    budget.setId(generatedKeys.getLong(1));
                }
                generatedKeys.close();
            }
            
            statement.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding budget: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing budget in the database
     * @param budget The budget to update
     * @return true if successful, false otherwise
     */
    public boolean updateBudget(Budget budget) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE budgets SET amount = ? WHERE id = ?"
            );
            
            statement.setDouble(1, budget.getAmount().doubleValue());
            statement.setLong(2, budget.getId());
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating budget: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the total expense amount for a specific category
     * @param categoryId The category ID
     * @return The total amount spent in this category
     */
    public BigDecimal getExpenseAmountForCategory(long categoryId) {
        BigDecimal amount = BigDecimal.ZERO;
        
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT SUM(amount) as total FROM transactions " +
                "WHERE category_id = ? AND type = 'Expense'"
            );
            
            statement.setLong(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next() && resultSet.getObject("total") != null) {
                amount = BigDecimal.valueOf(resultSet.getDouble("total"));
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error calculating category expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return amount;
    }
}
