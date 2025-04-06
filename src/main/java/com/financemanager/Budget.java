package com.financemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Model class representing a budget for a specific category
 */
public class Budget {
    private long id;
    private long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal spent;
    
    /**
     * Default constructor
     */
    public Budget() {
        this.amount = BigDecimal.ZERO;
        this.spent = BigDecimal.ZERO;
    }
    
    /**
     * Full constructor
     * 
     * @param id Budget ID
     * @param categoryId Category ID
     * @param categoryName Category name
     * @param amount Budget amount
     */
    public Budget(long id, long categoryId, String categoryName, BigDecimal amount) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.spent = BigDecimal.ZERO;
    }
    
    // Getters and setters
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getSpent() {
        return spent;
    }
    
    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }
    
    /**
     * Calculates the progress percentage of budget spending
     * @return Progress as a percentage (0-100)
     */
    public double getProgress() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        return spent.divide(amount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    /**
     * Calculates the remaining budget
     * @return Remaining amount
     */
    public BigDecimal getRemaining() {
        return amount.subtract(spent);
    }
    
    @Override
    public String toString() {
        return categoryName + ": $" + amount + " (Spent: $" + spent + ")";
    }
}
