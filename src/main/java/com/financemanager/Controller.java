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
    @FXML
    public void initialize() {
        // Initialize transaction types combo box with Russian labels
        transactionTypeComboBox.setItems(FXCollections.observableArrayList("Доход", "Расход"));
        transactionTypeComboBox.getSelectionModel().selectFirst();
        
        // Initialize date picker with current date
        datePicker.setValue(LocalDate.now());
        
        // Configure date format for date picker
        datePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
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
                    if ("Доход".equals(item)) {
                        getStyleClass().add("income-cell");
                    } else if ("Расход".equals(item)) {
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
                    setText(item.toString() + " ₽");
                    // Проверка на наличие транзакции
                    if (getTableView().getItems() != null && getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        Transaction transaction = getTableView().getItems().get(getIndex());
                        if (transaction != null && "Доход".equals(transaction.getType())) {
                            getStyleClass().add("income-cell");
                        } else if (transaction != null && "Расход".equals(transaction.getType())) {
                            getStyleClass().add("expense-cell");
                        }
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
            if (dataAccess != null) {
                updateCategoryComboBox();
            }
        });
        
        // Setup initial labels
        totalIncomeLabel.setText("Общий доход: 0.00 ₽");
        totalExpenseLabel.setText("Общие расходы: 0.00 ₽");
        balanceLabel.setText("Баланс: 0.00 ₽");
        
        // Apply CSS styles to labels
        totalIncomeLabel.getStyleClass().add("income-cell");
        totalExpenseLabel.getStyleClass().add("expense-cell");
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
        if (categories == null || categories.isEmpty() || transactionTypeComboBox.getValue() == null) {
            return;
        }
        
        String selectedType = transactionTypeComboBox.getValue();
        // Translate UI labels to database values
        String typeForFilter = "Доход".equals(selectedType) ? "Income" : "Expense";
        
        ObservableList<Category> filteredCategories = categories.filtered(
            category -> category.getType().equals(typeForFilter)
        );
        categoryComboBox.setItems(filteredCategories);
        if (!filteredCategories.isEmpty()) {
            categoryComboBox.getSelectionModel().selectFirst();
        }
        
        // Also update budget category combo box - only show expense categories
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
        
        // Translate transaction types from English to Russian
        for (Transaction transaction : transactionList) {
            if ("Income".equals(transaction.getType())) {
                transaction.setType("Доход");
            } else if ("Expense".equals(transaction.getType())) {
                transaction.setType("Расход");
            }
        }
        
        transactions.addAll(transactionList);
        transactionTable.setItems(transactions);
        updateSummary();
        updateCharts();
    }
    
    /**
     * Loads category data from the database
     */
    private void loadCategories() {
        if (dataAccess == null) {
            System.err.println("Data access is null in loadCategories()");
            return;
        }
        
        List<Category> categoryList = dataAccess.getAllCategories();
        categories.clear();
        
        // If no categories exist, create default ones (in Russian)
        if (categoryList.isEmpty()) {
            // Create default income categories
            dataAccess.addCategory(new Category("Зарплата", "Income"));
            dataAccess.addCategory(new Category("Премия", "Income"));
            dataAccess.addCategory(new Category("Инвестиции", "Income"));
            dataAccess.addCategory(new Category("Подарок", "Income"));
            dataAccess.addCategory(new Category("Прочие доходы", "Income"));
            
            // Create default expense categories
            dataAccess.addCategory(new Category("Продукты", "Expense"));
            dataAccess.addCategory(new Category("Жилье", "Expense"));
            dataAccess.addCategory(new Category("Транспорт", "Expense"));
            dataAccess.addCategory(new Category("Развлечения", "Expense"));
            dataAccess.addCategory(new Category("Коммунальные услуги", "Expense"));
            dataAccess.addCategory(new Category("Здоровье", "Expense"));
            dataAccess.addCategory(new Category("Образование", "Expense"));
            dataAccess.addCategory(new Category("Рестораны", "Expense"));
            dataAccess.addCategory(new Category("Одежда", "Expense"));
            dataAccess.addCategory(new Category("Техника", "Expense"));
            dataAccess.addCategory(new Category("Путешествия", "Expense"));
            dataAccess.addCategory(new Category("Подарки", "Expense"));
            dataAccess.addCategory(new Category("Прочие расходы", "Expense"));
            
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
            if ("Доход".equals(transaction.getType())) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else if ("Расход".equals(transaction.getType())) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        
        BigDecimal balance = totalIncome.subtract(totalExpense);
        
        totalIncomeLabel.setText("Общий доход: " + totalIncome + " ₽");
        totalExpenseLabel.setText("Общие расходы: " + totalExpense + " ₽");
        balanceLabel.setText("Баланс: " + balance + " ₽");
        
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
        
        // Update chart titles
        expenseChart.setTitle("Структура расходов");
        incomeChart.setTitle("Структура доходов");
        
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
            showAlert("Дата обязательна");
            return false;
        }
        
        if (transactionTypeComboBox.getValue() == null) {
            showAlert("Тип транзакции обязателен");
            return false;
        }
        
        if (categoryComboBox.getValue() == null) {
            showAlert("Категория обязательна");
            return false;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Сумма должна быть больше нуля");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Пожалуйста, введите корректную сумму");
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
        alert.setTitle("Ошибка ввода");
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
        
        // Convert between Russian UI labels and English database values
        String selectedType = transactionTypeComboBox.getValue();
        String dbType = "Доход".equals(selectedType) ? "Income" : "Expense";
        transaction.setType(dbType);
        
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
        
        // Convert between Russian UI labels and English database values
        String selectedType = transactionTypeComboBox.getValue();
        String dbType = "Доход".equals(selectedType) ? "Income" : "Expense";
        selectedTransaction.setType(dbType);
        
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
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText(null);
        alert.setContentText("Вы уверены, что хотите удалить эту транзакцию?");
        
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
            showAlert("Пожалуйста, выберите категорию");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(budgetAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Сумма бюджета должна быть больше нуля");
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
            showAlert("Пожалуйста, введите корректную сумму");
        }
    }
}
