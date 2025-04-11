package com.financemanager;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model class representing a financial transaction
 */
public class Transaction {
    private long id;
    private LocalDate date;
    private String type; // "Income" or "Expense"
    private long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    
    /**
     * Default constructor
     */
    public Transaction() {
        this.date = LocalDate.now();
        this.amount = BigDecimal.ZERO;
        this.description = "";
    }
    
    /**
     * Full constructor for Transaction
     * 
     * @param id Transaction ID
     * @param date Transaction date
     * @param type Transaction type (Income/Expense)
     * @param categoryId Category ID
     * @param categoryName Category name
     * @param amount Transaction amount
     * @param description Transaction description
     */
    public Transaction(long id, LocalDate date, String type, long categoryId, 
                      String categoryName, BigDecimal amount, String description) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and setters
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return date + " - " + type + " - " + categoryName + " - " + amount + " ₽ - " + description;
    }
}
