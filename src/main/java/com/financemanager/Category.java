package com.financemanager;

/**
 * Model class representing a transaction category
 */
public class Category {
    private long id;
    private String name;
    private String type; // "Income" or "Expense"
    
    /**
     * Default constructor
     */
    public Category() {
    }
    
    /**
     * Constructor with name and type
     * 
     * @param name Category name
     * @param type Category type (Income/Expense)
     */
    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
    /**
     * Full constructor
     * 
     * @param id Category ID
     * @param name Category name
     * @param type Category type (Income/Expense)
     */
    public Category(long id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
    
    // Getters and setters
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
