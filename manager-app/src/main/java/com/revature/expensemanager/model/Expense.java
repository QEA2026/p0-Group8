package com.revature.expensemanager.model;

public class Expense {
    private int id;
    private int userId; // FK -> users.id
    private double amount;
    private String description;
    private String category;
    private String date;

    // No-args constructor
    public Expense() {
    }

    // Constructor for new expenses before insertion
    public Expense(int userId, double amount, String description, String category, String date) {
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    // Constructor for existing expenses from database
    public Expense(int id, int userId, double amount, String description, String category, String date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense [id=" + id + ", userId=" + userId + ", amount=" + amount + ", description=" + description
                + ", category=" + category + ", date=" + date + "]";
    }
}
