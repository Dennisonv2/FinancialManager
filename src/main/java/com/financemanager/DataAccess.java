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
    /**
     * Main method for testing database connection
     * This is used by the test_finance_manager.sh script
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Check if called with "test" argument
        if (args.length > 0 && args[0].equals("test")) {
            DataAccess dataAccess = new DataAccess();
            try {
                // Test database connection
                dataAccess.connect();
                System.out.println("Database connection established successfully");
                
                // Test table creation
                dataAccess.createTablesIfNotExist();
                System.out.println("Database tables created/verified successfully");
                
                // Test retrieving categories
                List<Category> categories = dataAccess.getAllCategories();
                System.out.println("Successfully retrieved " + categories.size() + " categories");
                
                // Close connection
                dataAccess.disconnect();
                System.out.println("Database connection closed");
                
                // Exit with success code
                System.exit(0);
            } catch (Exception e) {
                System.err.println("Database test failed: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
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
            System.out.println("Tables created (if they didn't exist)");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Adds a new transaction to the database
     * 
     * @param transaction The transaction to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO transactions (date, type, category_id, amount, description) VALUES (?, ?, ?, ?, ?)"
            );
            
            statement.setString(1, transaction.getDate().toString());
            statement.setString(2, transaction.getType());
            statement.setLong(3, transaction.getCategoryId());
            statement.setBigDecimal(4, transaction.getAmount());
            statement.setString(5, transaction.getDescription());
            
            int rowsAffected = statement.executeUpdate();
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
     * 
     * @param transaction The transaction with updated values
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateTransaction(Transaction transaction) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE transactions SET date = ?, type = ?, category_id = ?, amount = ?, description = ? WHERE id = ?"
            );
            
            statement.setString(1, transaction.getDate().toString());
            statement.setString(2, transaction.getType());
            statement.setLong(3, transaction.getCategoryId());
            statement.setBigDecimal(4, transaction.getAmount());
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
     * 
     * @param transactionId The ID of the transaction to delete
     * @return true if the operation was successful, false otherwise
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
     * Retrieves all transactions from the database
     * 
     * @return A list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT t.id, t.date, t.type, t.category_id, c.name, t.amount, t.description " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "ORDER BY date DESC"
            );
            
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));
                String type = resultSet.getString("type");
                long categoryId = resultSet.getLong("category_id");
                String categoryName = resultSet.getString("name");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                String description = resultSet.getString("description");
                
                Transaction transaction = new Transaction(id, date, type, categoryId, categoryName, amount, description);
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
     * Adds a new category to the database
     * 
     * @param category The category to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addCategory(Category category) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO categories (name, type) VALUES (?, ?)"
            );
            
            statement.setString(1, category.getName());
            statement.setString(2, category.getType());
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates an existing category in the database
     * 
     * @param category The category with updated values
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE categories SET name = ? WHERE id = ?"
            );
            
            statement.setString(1, category.getName());
            statement.setLong(2, category.getId());
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a category from the database
     * 
     * @param categoryId The ID of the category to delete
     * @return true if the operation was successful, false otherwise
     */
    public boolean deleteCategory(long categoryId) {
        try {
            // Check if there are any transactions using this category
            PreparedStatement checkTransactions = connection.prepareStatement(
                "SELECT COUNT(*) FROM transactions WHERE category_id = ?"
            );
            checkTransactions.setLong(1, categoryId);
            ResultSet rs1 = checkTransactions.executeQuery();
            if (rs1.next() && rs1.getInt(1) > 0) {
                System.err.println("Cannot delete category: There are transactions using this category");
                rs1.close();
                checkTransactions.close();
                return false;
            }
            rs1.close();
            checkTransactions.close();
            
            // Check if there are any budgets using this category
            PreparedStatement checkBudgets = connection.prepareStatement(
                "SELECT COUNT(*) FROM budgets WHERE category_id = ?"
            );
            checkBudgets.setLong(1, categoryId);
            ResultSet rs2 = checkBudgets.executeQuery();
            if (rs2.next() && rs2.getInt(1) > 0) {
                System.err.println("Cannot delete category: There are budgets using this category");
                rs2.close();
                checkBudgets.close();
                return false;
            }
            rs2.close();
            checkBudgets.close();
            
            // Delete the category
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM categories WHERE id = ?"
            );
            
            statement.setLong(1, categoryId);
            
            int rowsAffected = statement.executeUpdate();
            statement.close();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all categories from the database
     * 
     * @return A list of all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT id, name, type FROM categories ORDER BY type, name"
            );
            
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                
                Category category = new Category(id, name, type);
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
     * Adds a new budget to the database
     * 
     * @param budget The budget to add
     * @return true if the operation was successful, false otherwise
     */
    public boolean addBudget(Budget budget) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO budgets (category_id, amount) VALUES (?, ?)"
            );
            
            statement.setLong(1, budget.getCategoryId());
            statement.setBigDecimal(2, budget.getAmount());
            
            int rowsAffected = statement.executeUpdate();
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
     * 
     * @param budget The budget with updated values
     * @return true if the operation was successful, false otherwise
     */
    public boolean updateBudget(Budget budget) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE budgets SET amount = ? WHERE id = ?"
            );
            
            statement.setBigDecimal(1, budget.getAmount());
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
     * Retrieves all budgets from the database with their category names
     * 
     * @return A list of all budgets with category information
     */
    public List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                "SELECT b.id, b.category_id, c.name AS category_name, b.amount " +
                "FROM budgets b " +
                "JOIN categories c ON b.category_id = c.id " +
                "ORDER BY c.name"
            );
            
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                long categoryId = resultSet.getLong("category_id");
                String categoryName = resultSet.getString("category_name");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                
                // Calculate spent amount from transactions
                BigDecimal spent = getExpenseAmountForCategory(categoryId);
                
                // Calculate progress percentage
                double progress = 0;
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    progress = spent.multiply(new BigDecimal(100))
                        .divide(amount, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                }
                
                Budget budget = new Budget(id, categoryId, categoryName, amount, spent, progress);
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
     * Gets the total expense amount for a specific category
     * 
     * @param categoryId The category ID to calculate expenses for
     * @return The total expense amount for the category
     */
    public BigDecimal getExpenseAmountForCategory(long categoryId) {
        BigDecimal total = BigDecimal.ZERO;
        
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT SUM(amount) FROM transactions WHERE category_id = ? AND type = 'Expense'"
            );
            
            statement.setLong(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next() && resultSet.getObject(1) != null) {
                total = resultSet.getBigDecimal(1);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            System.err.println("Error calculating category expenses: " + e.getMessage());
            e.printStackTrace();
        }
        
        return total;
    }
}
