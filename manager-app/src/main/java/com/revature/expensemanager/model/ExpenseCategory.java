package com.revature.expensemanager.model;

public enum ExpenseCategory {
    TRAVEL,
    MEALS,
    LODGING,
    OFFICE_SUPPLIES,
    EQUIPMENT,
    SOFTWARE,
    TRAINING,
    OTHER;

    public static ExpenseCategory fromInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Category is required.");
        }

        String normalized = input.trim().toUpperCase();

        try {
            return ExpenseCategory.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category.");
        }
    }

    public static String validCategoriesMessage() {
        return "Valid categories: TRAVEL, MEALS, LODGING, OFFICE_SUPPLIES, EQUIPMENT, SOFTWARE, TRAINING, OTHER.";
    }
}
