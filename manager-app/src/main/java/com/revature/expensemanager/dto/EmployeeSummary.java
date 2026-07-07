package com.revature.expensemanager.dto;

public class EmployeeSummary {

    private int id;
    private String username;

    public EmployeeSummary() {
    }

    public EmployeeSummary(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}