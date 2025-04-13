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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    @FXML private Button updateBudgetButton;
    @FXML private TableView<Budget> budgetTable;
    @FXML private TableColumn<Budget, String> budgetCategoryColumn;
    @FXML private TableColumn<Budget, BigDecimal> budgetAmountColumn;
    @FXML private TableColumn<Budget, BigDecimal> budgetSpentColumn;
    @FXML private TableColumn<Budget, Double> budgetProgressColumn;
    
    // Add UI components for category management
    @FXML private TextField newCategoryNameField;
    @FXML private ComboBox<String> newCategoryTypeComboBox;
    @FXML private Button addCategoryButton;
    @FXML private Button updateCategoryButton;
    @FXML private Button deleteCategoryButton;
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> categoryNameColumn;
    @FXML private TableColumn<Category, String> categoryTypeColumn;
    
    // Data model
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private ObservableList<Budget> budgets = FXCollections.observableArrayList();
    
    // Selected transaction for update/delete
    private Transaction selectedTransaction;
    
    // Selected budget for update
    private Budget selectedBudget;
    
    // Selected category for update/delete
    private Category selectedCategory;
    
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
        
        // Set up progress bar display for budget progress
        budgetProgressColumn.setCellFactory(column -> new TableCell<Budget, Double>() {
            @Override
            protected void updateItem(Double progress, boolean empty) {
                super.updateItem(progress, empty);
                
                if (progress == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Create progress bar
                    ProgressBar bar = new ProgressBar(progress / 100.0);
                    bar.setPrefWidth(getWidth() - 40);
                    
                    // Create label for progress percentage
                    Label percentLabel = new Label(String.format("%.1f%%", progress));
                    
                    // Create a stack pane to overlay the label on the progress bar
                    StackPane stackPane = new StackPane();
                    stackPane.getChildren().addAll(bar, percentLabel);
                    stackPane.setPrefWidth(getWidth() - 20);
                    
                    // Set the graphic to show the stack pane
                    setGraphic(stackPane);
                    setText(null);
                    
                    // Apply color styling based on progress
                    if (progress >= 90) {
                        bar.setStyle("-fx-accent: #ff6347;"); // Red if close to or over budget
                    } else if (progress >= 75) {
                        bar.setStyle("-fx-accent: #ffa500;"); // Orange if getting close
                    } else {
                        bar.setStyle("-fx-accent: #7cfc00;"); // Green if well within budget
                    }
                }
            }
        });
        
        // Set up category table columns
        if (categoryNameColumn != null && categoryTypeColumn != null) {
            categoryNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            categoryTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            
            // Set up a cell factory for type column to display translated types
            categoryTypeColumn.setCellFactory(column -> new TableCell<Category, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if ("Income".equals(item)) {
                            setText("Доход");
                        } else if ("Expense".equals(item)) {
                            setText("Расход");
                        } else {
                            setText(item);
                        }
                    }
                }
            });
        }
        
        // Initialize category type combo box (if exists)
        if (newCategoryTypeComboBox != null) {
            newCategoryTypeComboBox.setItems(FXCollections.observableArrayList("Доход", "Расход"));
            newCategoryTypeComboBox.getSelectionModel().selectFirst();
        }
        
        // Add selection listener to category table
        if (categoryTable != null) {
            categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedCategory = newSelection;
                    populateFormWithCategory(selectedCategory);
                    if (updateCategoryButton != null) {
                        updateCategoryButton.setDisable(false);
                    }
                    if (deleteCategoryButton != null) {
                        deleteCategoryButton.setDisable(false);
                    }
                } else {
                    if (updateCategoryButton != null) {
                        updateCategoryButton.setDisable(true);
                    }
                    if (deleteCategoryButton != null) {
                        deleteCategoryButton.setDisable(true);
                    }
                }
            });
        }
        
        // Initially disable update and delete category buttons
        if (updateCategoryButton != null) {
            updateCategoryButton.setDisable(true);
        }
        if (deleteCategoryButton != null) {
            deleteCategoryButton.setDisable(true);
        }
        
        // Add selection listener to transaction table
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
        
        // Add selection listener to budget table
        budgetTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBudget = newSelection;
                populateFormWithBudget(selectedBudget);
                if (updateBudgetButton != null) {
                    updateBudgetButton.setDisable(false);
                }
            } else {
                selectedBudget = null;
                if (updateBudgetButton != null) {
                    updateBudgetButton.setDisable(true);
                }
            }
        });
        
        // Initially disable update and delete buttons
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        if (updateBudgetButton != null) {
            updateBudgetButton.setDisable(true);
        }
        
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
        
        // Update category table if it exists
        if (categoryTable != null) {
            categoryTable.setItems(categories);
        }
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
        
        // Update charts
        expenseChart.setData(expenseData);
        incomeChart.setData(incomeData);
    }
    
    /**
     * Populates the form with data from the selected transaction
     * @param transaction The transaction to display
     */
    private void populateFormWithTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        
        datePicker.setValue(transaction.getDate());
        transactionTypeComboBox.setValue(transaction.getType());
        amountField.setText(transaction.getAmount().toString());
        descriptionField.setText(transaction.getDescription());
        
        // Find and select the correct category
        updateCategoryComboBox(); // Make sure categories are filtered
        for (Category category : categoryComboBox.getItems()) {
            if (category.getId() == transaction.getCategoryId()) {
                categoryComboBox.setValue(category);
                break;
            }
        }
    }
    
    /**
     * Populates the budget form with data from the selected budget
     * @param budget The budget to display
     */
    private void populateFormWithBudget(Budget budget) {
        if (budget == null) {
            return;
        }
        
        budgetAmountField.setText(budget.getAmount().toString());
        
        // Find and select the correct category
        for (Category category : budgetCategoryComboBox.getItems()) {
            if (category.getId() == budget.getCategoryId()) {
                budgetCategoryComboBox.setValue(category);
                break;
            }
        }
    }
    
    /**
     * Adds a new transaction
     * @param event The event that triggered this action
     */
    @FXML
    private void handleAddTransaction(ActionEvent event) {
        if (validateTransactionInput()) {
            Transaction transaction = createTransactionFromForm();
            
            // Map Russian type names to English for database
            if ("Доход".equals(transaction.getType())) {
                transaction.setType("Income");
            } else if ("Расход".equals(transaction.getType())) {
                transaction.setType("Expense");
            }
            
            if (dataAccess.addTransaction(transaction)) {
                showAlert("Транзакция успешно добавлена");
                clearTransactionForm();
                loadTransactions(); // Reload transactions
            } else {
                showAlert("Ошибка при добавлении транзакции");
            }
        }
    }
    
    /**
     * Updates the selected transaction
     * @param event The event that triggered this action
     */
    @FXML
    private void handleUpdateTransaction(ActionEvent event) {
        if (selectedTransaction == null) {
            showAlert("Пожалуйста, выберите транзакцию для обновления");
            return;
        }
        
        if (validateTransactionInput()) {
            Transaction transaction = createTransactionFromForm();
            transaction.setId(selectedTransaction.getId());
            
            // Map Russian type names to English for database
            if ("Доход".equals(transaction.getType())) {
                transaction.setType("Income");
            } else if ("Расход".equals(transaction.getType())) {
                transaction.setType("Expense");
            }
            
            if (dataAccess.updateTransaction(transaction)) {
                showAlert("Транзакция успешно обновлена");
                clearTransactionForm();
                loadTransactions(); // Reload transactions
            } else {
                showAlert("Ошибка при обновлении транзакции");
            }
        }
    }
    
    /**
     * Deletes the selected transaction
     * @param event The event that triggered this action
     */
    @FXML
    private void handleDeleteTransaction(ActionEvent event) {
        if (selectedTransaction == null) {
            showAlert("Пожалуйста, выберите транзакцию для удаления");
            return;
        }
        
        if (dataAccess.deleteTransaction(selectedTransaction.getId())) {
            showAlert("Транзакция успешно удалена");
            clearTransactionForm();
            loadTransactions(); // Reload transactions
        } else {
            showAlert("Ошибка при удалении транзакции");
        }
    }
    
    /**
     * Clears the transaction form fields
     * @param event The event that triggered this action
     */
    @FXML
    private void handleClearForm(ActionEvent event) {
        clearTransactionForm();
    }
    
    /**
     * Clears the transaction form fields and resets selection
     */
    private void clearTransactionForm() {
        datePicker.setValue(LocalDate.now());
        transactionTypeComboBox.getSelectionModel().selectFirst();
        updateCategoryComboBox();
        amountField.clear();
        descriptionField.clear();
        
        // Clear selection
        transactionTable.getSelectionModel().clearSelection();
        selectedTransaction = null;
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }
    
    /**
     * Creates a Transaction object from form inputs
     * @return The created Transaction
     */
    private Transaction createTransactionFromForm() {
        LocalDate date = datePicker.getValue();
        String type = transactionTypeComboBox.getValue();
        Category selectedCategory = categoryComboBox.getValue();
        BigDecimal amount = new BigDecimal(amountField.getText());
        String description = descriptionField.getText();
        
        return new Transaction(
            0, // ID will be set by the database
            date,
            type,
            selectedCategory.getId(),
            selectedCategory.getName(),
            amount,
            description
        );
    }
    
    /**
     * Validates the transaction input fields
     * @return true if valid, false otherwise
     */
    private boolean validateTransactionInput() {
        if (datePicker.getValue() == null) {
            showAlert("Пожалуйста, выберите дату");
            return false;
        }
        
        if (categoryComboBox.getValue() == null) {
            showAlert("Пожалуйста, выберите категорию");
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
     * Adds a new budget or updates an existing one
     * @param event The event that triggered this action
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
            
            showAlert("Бюджет успешно " + (budgetExists ? "обновлен" : "добавлен"));
            clearBudgetForm();
            loadBudgets(); // Reload budgets
        } catch (NumberFormatException e) {
            showAlert("Пожалуйста, введите корректную сумму бюджета");
        }
    }
    
    /**
     * Updates the selected budget
     * @param event The event that triggered this action
     */
    @FXML
    private void handleUpdateBudget(ActionEvent event) {
        if (selectedBudget == null) {
            showAlert("Пожалуйста, выберите бюджет для обновления");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(budgetAmountField.getText());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert("Сумма бюджета должна быть больше нуля");
                return;
            }
            
            selectedBudget.setAmount(amount);
            
            if (dataAccess.updateBudget(selectedBudget)) {
                showAlert("Бюджет успешно обновлен");
                clearBudgetForm();
                loadBudgets(); // Reload budgets
            } else {
                showAlert("Ошибка при обновлении бюджета");
            }
        } catch (NumberFormatException e) {
            showAlert("Пожалуйста, введите корректную сумму бюджета");
        }
    }
    
    /**
     * Clears the budget form fields and resets selection
     */
    private void clearBudgetForm() {
        budgetAmountField.clear();
        if (!budgetCategoryComboBox.getItems().isEmpty()) {
            budgetCategoryComboBox.getSelectionModel().selectFirst();
        }
        
        // Clear selection
        budgetTable.getSelectionModel().clearSelection();
        selectedBudget = null;
        if (updateBudgetButton != null) {
            updateBudgetButton.setDisable(true);
        }
    }
    
    /**
     * Adds a new category
     * @param event The event that triggered this action
     */
    @FXML
    private void handleAddCategory(ActionEvent event) {
        if (newCategoryNameField == null || newCategoryTypeComboBox == null) {
            showAlert("Форма добавления категории недоступна");
            return;
        }
        
        String name = newCategoryNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            showAlert("Пожалуйста, введите название категории");
            return;
        }
        
        String uiType = newCategoryTypeComboBox.getValue();
        if (uiType == null) {
            showAlert("Пожалуйста, выберите тип категории");
            return;
        }
        
        // Convert UI type to database type
        String dbType = "Доход".equals(uiType) ? "Income" : "Expense";
        
        Category category = new Category(name, dbType);
        
        if (dataAccess.addCategory(category)) {
            showAlert("Категория успешно добавлена");
            newCategoryNameField.clear();
            loadCategories(); // Reload categories
            updateCategoryComboBox(); // Update combo boxes
        } else {
            showAlert("Ошибка при добавлении категории");
        }
    }
    
    /**
     * Обработчик для изменения названия категории
     * @param event Событие нажатия на кнопку
     */
    @FXML
    private void handleUpdateCategory(ActionEvent event) {
        if (selectedCategory == null) {
            showAlert("Пожалуйста, выберите категорию для изменения");
            return;
        }
        
        String newName = newCategoryNameField.getText();
        if (newName == null || newName.trim().isEmpty()) {
            showAlert("Пожалуйста, введите новое название категории");
            return;
        }
        
        selectedCategory.setName(newName);
        
        if (dataAccess.updateCategory(selectedCategory)) {
            showAlert("Категория успешно обновлена");
            clearCategoryForm();
            loadCategories(); // Перезагрузка категорий
            updateCategoryComboBox(); // Обновление выпадающих списков
        } else {
            showAlert("Ошибка при обновлении категории");
        }
    }
    
    /**
     * Обработчик для удаления категории
     * @param event Событие нажатия на кнопку
     */
    @FXML
    private void handleDeleteCategory(ActionEvent event) {
        if (selectedCategory == null) {
            showAlert("Пожалуйста, выберите категорию для удаления");
            return;
        }
        
        // Проверка, можно ли удалить категорию (нет связанных транзакций или бюджетов)
        boolean hasTransactions = transactions.stream()
                .anyMatch(t -> t.getCategoryId() == selectedCategory.getId());
        
        boolean hasBudgets = budgets.stream()
                .anyMatch(b -> b.getCategoryId() == selectedCategory.getId());
        
        if (hasTransactions || hasBudgets) {
            StringBuilder message = new StringBuilder("Невозможно удалить категорию, так как она используется в ");
            if (hasTransactions) message.append("транзакциях");
            if (hasTransactions && hasBudgets) message.append(" и ");
            if (hasBudgets) message.append("бюджетах");
            
            showAlert(message.toString());
            return;
        }
        
        if (dataAccess.deleteCategory(selectedCategory.getId())) {
            showAlert("Категория успешно удалена");
            clearCategoryForm();
            loadCategories(); // Перезагрузка категорий
            updateCategoryComboBox(); // Обновление выпадающих списков
        } else {
            showAlert("Ошибка при удалении категории");
        }
    }
    
    /**
     * Заполняет форму данными выбранной категории
     * @param category Категория для отображения
     */
    private void populateFormWithCategory(Category category) {
        if (category == null) {
            return;
        }
        
        newCategoryNameField.setText(category.getName());
        
        // Выбор типа категории в выпадающем списке
        if (newCategoryTypeComboBox != null) {
            if ("Income".equals(category.getType())) {
                newCategoryTypeComboBox.setValue("Доход");
            } else if ("Expense".equals(category.getType())) {
                newCategoryTypeComboBox.setValue("Расход");
            }
        }
    }
    
    /**
     * Очищает форму категории и сбрасывает выбор
     */
    private void clearCategoryForm() {
        if (newCategoryNameField != null) {
            newCategoryNameField.clear();
        }
        
        if (newCategoryTypeComboBox != null && !newCategoryTypeComboBox.getItems().isEmpty()) {
            newCategoryTypeComboBox.getSelectionModel().selectFirst();
        }
        
        // Очистка выбора
        if (categoryTable != null) {
            categoryTable.getSelectionModel().clearSelection();
        }
        selectedCategory = null;
        
        // Блокировка кнопок изменения и удаления
        if (updateCategoryButton != null) {
            updateCategoryButton.setDisable(true);
        }
        if (deleteCategoryButton != null) {
            deleteCategoryButton.setDisable(true);
        }
    }
    
    /**
     * Shows an alert dialog with the given message
     * @param message The message to display
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
