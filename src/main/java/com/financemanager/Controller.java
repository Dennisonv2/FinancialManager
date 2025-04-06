package com.financemanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller class to handle UI interactions and business logic
 */
public class Controller {
    
    // UI Components
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Long> idColumn;
    @FXML private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, String> categoryColumn;
    @FXML private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    
    @FXML private ComboBox<String> transactionTypeComboBox;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;
    
    @FXML private Label totalIncomeLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label balanceLabel;
    
    @FXML private PieChart expenseChart;
    @FXML private PieChart incomeChart;
    
    @FXML private TextField budgetAmountField;
    @FXML private ComboBox<Category> budgetCategoryComboBox;
    @FXML private Button addBudgetButton;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> budgetCategoryColumn;
    @FXML private TableColumn<Budget, BigDecimal> budgetAmountColumn;
    @FXML private TableColumn<Budget, BigDecimal> budgetSpentColumn;
    @FXML private TableColumn<Budget, Double> budgetProgressColumn;
    
    // Data model
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Budget> budgets = FXCollections.observableArrayList();
    
    // Selected transaction for update/delete
    private Transaction selectedTransaction;
    
    // Data access object
    private DataAccess dataAccess;
    
    /**
     * Sets the data access object for database operations
     * @param dataAccess The data access object
     */
    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        loadData(); // Load data once data access is set
    }
    
    /**
     * Initializes the controller and UI elements
     */
    public void initialize() {
        // Initialize transaction types combo box
        transactionTypeComboBox.setItems(FXCollections.observableArrayList("Income", "Expense"));
        transactionTypeComboBox.getSelectionModel().selectFirst();
        
        // Initialize date picker with current date
        datePicker.setValue(LocalDate.now());
        
        // Configure date format for date picker
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Apply color formatting to rows based on transaction type
        typeColumn.setCellFactory(column -> new TableCell<Transaction, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Income".equals(item)) {
                        getStyleClass().add("income-cell");
                    } else if ("Expense".equals(item)) {
                        getStyleClass().add("expense-cell");
                    }
                }
            }
        });
        
        // Apply color formatting to amount based on transaction type
        amountColumn.setCellFactory(column -> new TableCell<Transaction, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    if ("Income".equals(transaction.getType())) {
                        getStyleClass().add("income-cell");
                    } else if ("Expense".equals(transaction.getType())) {
                        getStyleClass().add("expense-cell");
                    }
                }
            }
        });
        
        // Set up budget table columns
        budgetCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        budgetAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        budgetSpentColumn.setCellValueFactory(new PropertyValueFactory<>("spent"));
        budgetProgressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        
        // Add selection listener to table
        transactionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTransaction = newSelection;
                populateFormWithTransaction(selectedTransaction);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        
        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Track transaction type selection change to filter categories
        transactionTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateCategoryComboBox();
        });
    }
    
    /**
     * Loads all data from the database once the dataAccess is set
     * This should be called after setDataAccess
     */
    public void loadData() {
        if (dataAccess == null) {
            return;
        }
        
        // Load data from database
        loadCategories();
        loadTransactions();
        loadBudgets();
        
        // Set up charts
        updateCharts();
        
        // Initial update of category combo box
        updateCategoryComboBox();
    }
    
    /**
     * Updates the category combo box based on the selected transaction type
     */
    private void updateCategoryComboBox() {
        String selectedType = transactionTypeComboBox.getValue();
        ObservableList<Category> filteredCategories = categories.filtered(
            category -> category.getType().equals(selectedType)
        );
        categoryComboBox.setItems(filteredCategories);
        if (!filteredCategories.isEmpty()) {
            categoryComboBox.getSelectionModel().selectFirst();
        }
        
        // Also update budget category combo box
        budgetCategoryComboBox.setItems(categories.filtered(
            category -> category.getType().equals("Expense")
        ));
        if (!budgetCategoryComboBox.getItems().isEmpty()) {
            budgetCategoryComboBox.getSelectionModel().selectFirst();
        }
    }
    
    /**
     * Loads transaction data from the database
     */
    private void loadTransactions() {
        List<Transaction> transactionList = dataAccess.getAllTransactions();
        transactions.clear();
        transactions.addAll(transactionList);
        transactionTable.setItems(transactions);
        updateSummary();
        updateCharts();
    }
    
    /**
     * Loads category data from the database
     */
    private void loadCategories() {
        List<Category> categoryList = dataAccess.getAllCategories();
        categories.clear();
        
        // If no categories exist, create default ones
        if (categoryList.isEmpty()) {
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
            
            // Reload categories
            categoryList = dataAccess.getAllCategories();
        }
        
        categories.addAll(categoryList);
    }
    
    /**
     * Loads budget data from the database
     */
    private void loadBudgets() {
        List<Budget> budgetList = dataAccess.getAllBudgets();
        budgets.clear();
        budgets.addAll(budgetList);
        budgetTable.setItems(budgets);
        
        // Calculate spent amounts for each budget
        for (Budget budget : budgets) {
            BigDecimal spent = dataAccess.getExpenseAmountForCategory(budget.getCategoryId());
            budget.setSpent(spent);
        }
    }
    
    /**
     * Updates the summary information (total income, expenses, balance)
     */
    private void updateSummary() {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Transaction transaction : transactions) {
            if ("Income".equals(transaction.getType())) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if ("Expense".equals(transaction.getType())) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        totalIncomeLabel.setText("Total Income: $" + totalIncome);
        totalExpenseLabel.setText("Total Expenses: $" + totalExpense);
        balanceLabel.setText("Balance: $" + balance);
        
        // Set color based on balance
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balanceLabel.setStyle("-fx-text-fill: #ff6347;"); // Red for negative balance
        } else {
            balanceLabel.setStyle("-fx-text-fill: #7cfc00;"); // Green for positive balance
        }
        
        // Also apply color to income and expense labels
        totalIncomeLabel.setStyle("-fx-text-fill: #7cfc00;"); // Green for income
        totalExpenseLabel.setStyle("-fx-text-fill: #ff6347;"); // Red for expenses
    }
    
    /**
     * Updates the pie charts for income and expenses
     */
    private void updateCharts() {
        // Create data for expense chart
        ObservableList<PieChart.Data> expenseData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> incomeData = FXCollections.observableArrayList();
        
        // Group transactions by category
        for (Category category : categories) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (Transaction transaction : transactions) {
                if (transaction.getCategoryId() == category.getId()) {
                    totalAmount = totalAmount.add(transaction.getAmount());
                }
            }
            
            // Only add categories with non-zero amounts
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                if ("Expense".equals(category.getType())) {
                    expenseData.add(new PieChart.Data(category.getName(), totalAmount.doubleValue()));
                } else if ("Income".equals(category.getType())) {
                    incomeData.add(new PieChart.Data(category.getName(), totalAmount.doubleValue()));
                }
            }
        }
        
        expenseChart.setData(expenseData);
        incomeChart.setData(incomeData);
    }
    
    /**
     * Populates the form with selected transaction data
     * @param transaction The selected transaction
     */
    private void populateFormWithTransaction(Transaction transaction) {
        datePicker.setValue(transaction.getDate());
        transactionTypeComboBox.setValue(transaction.getType());
        
        // Set category
        for (Category category : categories) {
            if (category.getId() == transaction.getCategoryId()) {
                categoryComboBox.setValue(category);
                break;
            }
        }
        
        amountField.setText(transaction.getAmount().toString());
        descriptionField.setText(transaction.getDescription());
    }
    
    /**
     * Clears the input form
     */
    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        transactionTypeComboBox.getSelectionModel().selectFirst();
        updateCategoryComboBox();
        amountField.clear();
        descriptionField.clear();
        selectedTransaction = null;
        transactionTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }
    
    /**
     * Validates the input form
     * @return True if input is valid, otherwise false
     */
    private boolean validateForm() {
        if (datePicker.getValue() == null) {
            showAlert("Date is required");
            return false;
        }
        
        if (transactionTypeComboBox.getValue() == null) {
            showAlert("Transaction type is required");
            return false;
        }
        
        if (categoryComboBox.getValue() == null) {
            showAlert("Category is required");
            return false;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Amount must be greater than zero");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid amount");
            return false;
        }
        
        return true;
    }
    
    /**
     * Shows an alert dialog with the specified message
     * @param message The message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Event handler for Add Transaction button
     * @param event The action event
     */
    @FXML
    private void handleAddTransaction(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        Transaction transaction = new Transaction();
        transaction.setDate(datePicker.getValue());
        transaction.setType(transactionTypeComboBox.getValue());
        transaction.setCategoryId(categoryComboBox.getValue().getId());
        transaction.setCategoryName(categoryComboBox.getValue().getName());
        transaction.setAmount(new BigDecimal(amountField.getText()));
        transaction.setDescription(descriptionField.getText());
        
        dataAccess.addTransaction(transaction);
        loadTransactions();
        clearForm();
    }
    
    /**
     * Event handler for Update Transaction button
     * @param event The action event
     */
    @FXML
    private void handleUpdateTransaction(ActionEvent event) {
        if (selectedTransaction == null) {
            return;
        }
        
        if (!validateForm()) {
            return;
        }
        
        selectedTransaction.setDate(datePicker.getValue());
        selectedTransaction.setType(transactionTypeComboBox.getValue());
        selectedTransaction.setCategoryId(categoryComboBox.getValue().getId());
        selectedTransaction.setCategoryName(categoryComboBox.getValue().getName());
        selectedTransaction.setAmount(new BigDecimal(amountField.getText()));
        selectedTransaction.setDescription(descriptionField.getText());
        
        dataAccess.updateTransaction(selectedTransaction);
        loadTransactions();
        clearForm();
    }
    
    /**
     * Event handler for Delete Transaction button
     * @param event The action event
     */
    @FXML
    private void handleDeleteTransaction(ActionEvent event) {
        if (selectedTransaction == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this transaction?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dataAccess.deleteTransaction(selectedTransaction.getId());
                loadTransactions();
                clearForm();
            }
        });
    }
    
    /**
     * Event handler for Clear Form button
     * @param event The action event
     */
    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }
    
    /**
     * Event handler for Add Budget button
     * @param event The action event
     */
    @FXML
    private void handleAddBudget(ActionEvent event) {
        if (budgetCategoryComboBox.getValue() == null) {
            showAlert("Please select a category");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(budgetAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Budget amount must be greater than zero");
                return;
            }
            
            Budget budget = new Budget();
            budget.setCategoryId(budgetCategoryComboBox.getValue().getId());
            budget.setCategoryName(budgetCategoryComboBox.getValue().getName());
            budget.setAmount(amount);
            
            // Check if a budget already exists for this category
            boolean budgetExists = false;
            for (Budget existingBudget : budgets) {
                if (existingBudget.getCategoryId() == budget.getCategoryId()) {
                    budgetExists = true;
                    
                    // Update existing budget
                    existingBudget.setAmount(amount);
                    dataAccess.updateBudget(existingBudget);
                    break;
                }
            }
            
            if (!budgetExists) {
                dataAccess.addBudget(budget);
            }
            
            loadBudgets();
            budgetAmountField.clear();
            
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid budget amount");
        }
    }
}
