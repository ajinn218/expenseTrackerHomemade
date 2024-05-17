package com.example.expensetrackerhomemade;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CategoryManager {
    private ObservableList<String> incomeCategories;
    private ObservableList<String> expenseCategories;

    public CategoryManager() {
        incomeCategories = FXCollections.observableArrayList("Salary", "Gift", "Investment");
        expenseCategories = FXCollections.observableArrayList("Groceries", "Rent", "Utilities");
    }

    public ObservableList<String> getIncomeCategories() {
        return incomeCategories;
    }

    public ObservableList<String> getExpenseCategories() {
        return expenseCategories;
    }

    public void addCategory(String category, String type) {
        if (type.equals("Income")) {
            incomeCategories.add(category);
        } else if (type.equals("Expense")) {
            expenseCategories.add(category);
        }
    }

    public void removeCategory(String category, String type) {
        if (type.equals("Income")) {
            incomeCategories.remove(category);
        } else if (type.equals("Expense")) {
            expenseCategories.remove(category);
        }
    }
}
