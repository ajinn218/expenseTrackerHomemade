package com.example.expensetrackerhomemade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String strDBUrl = "jdbc:mysql://localhost:3306/ExpenseTracker";
    private static final String strDbUser = "your_username";
    private static final String strDbPassword = "your_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(strDBUrl, strDbUser, strDbPassword);
    }
}
