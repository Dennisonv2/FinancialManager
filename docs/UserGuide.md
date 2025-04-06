# Finance Manager - User Guide

This guide will help you get started with the Finance Manager application and make the most of its features.

## Getting Started

### Installation

1. Install Java 17 or higher on your system if not already installed
2. Download the FinanceManager executable (JAR or EXE file)
3. Double-click the executable to launch the application

### First Run

On first launch, the application will:
- Create a new database file to store your financial data
- Set up initial categories for income and expenses

## Main Features

### Managing Transactions

#### Adding Income
1. From the main menu, select "Add Income"
2. Enter the date or accept the default (today)
3. Select a category (e.g., Salary, Freelance)
4. Enter the amount
5. Add an optional description
6. Confirm to save

#### Adding Expenses
1. From the main menu, select "Add Expense"
2. Enter the date or accept the default (today)
3. Select a category (e.g., Food, Transportation)
4. Enter the amount
5. Add an optional description
6. Confirm to save

#### Viewing Transactions
1. From the main menu, select "View Transactions"
2. Browse through your transaction history
3. Filter by date range, category, or transaction type (optional)

### Budget Management

#### Setting a Budget
1. From the main menu, select "Set Budget"
2. Choose a category
3. Enter the budget amount
4. Save your budget

#### Monitoring Your Budget
1. From the main menu, select "View Budget"
2. Review your spending against budget limits
3. See warnings for categories where you're close to or over budget

### Financial Summary

1. From the main menu, select "View Summary"
2. View income, expenses, and savings for the current month
3. See spending by category
4. Check monthly trends (if available)

## Tips for Effective Use

- Add transactions regularly to maintain accurate records
- Set realistic budgets based on your historical spending
- Use descriptive notes in transactions to help with future reference
- Review your financial summary at least once a month
- Adjust budgets as your financial situation changes

## Troubleshooting

### Database Issues
If you encounter database errors:
1. Make sure the database file (`finance_manager.db`) is in the same directory as the application
2. Ensure you have write permissions for the application directory
3. Try restarting the application

### Display Problems
If the GUI doesn't display correctly:
1. Make sure you have the correct Java version installed (Java 17+)
2. Try adjusting your screen resolution
3. If GUI won't work, you can always use the console version

### If All Else Fails
If you're experiencing persistent issues:
1. Try the Console version of the application if you were using the GUI version
2. Check if a newer version of the application is available
3. Contact support for further assistance

## Command Line Arguments (Advanced)

For advanced users, the application supports the following command line arguments:
- `--console`: Force console mode even if GUI is available
- `--db=PATH`: Specify a custom database path
- `--reset`: Reset the database (WARNING: Deletes all data)

Example:
```
java -jar FinanceManager.jar --console --db=my_finances.db
```
