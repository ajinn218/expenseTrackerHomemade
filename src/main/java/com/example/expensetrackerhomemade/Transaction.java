package com.example.expensetrackerhomemade;

// Гүйлгээний өгөгдлийн багцыг тодорхойлох
import java.time.LocalDate;

public class Transaction {
    private double amount; // Гүйлгээний дүн
    private LocalDate date; // Гүйлгээний огноо
    private String category; // Гүйлгээний ангилал

    // Шинэ гүйлгээ үүсгэх конструктор
    public Transaction(double amount, LocalDate date, String category) {
        this.amount = amount;
        this.date = date;
        this.category = category; // Ангиллыг ажиллуулах
    }

    // Гүйлгээний дүнгийн гэттэр, сэттэр функцүүд
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Гүйлгээний огноо гэттэр, сэттэр функцүүд
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Гүйлгээний ангиллын гэттэр, сэттэр функцүүд
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}