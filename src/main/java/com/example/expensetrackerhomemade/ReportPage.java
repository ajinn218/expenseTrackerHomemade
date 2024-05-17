/**
 *  @author Sortagjin
 * Хэрэглэгчийн тодорхойлсон хугацаанд үндэслэн санхүүгийн тайлан гаргадаг.
 * Зардал, орлого, төсвийн байдал зэрэг MySQL мэдээллийн сангаас өгөгдлийг татаж авдаг,
 * хамгийн их зарцуулсан ангилал, заасан хугацаанд хамгийн их орлого олдог эх сурвалжууд.
 * Профайл, төсөв гэх мэт бусад хуудсуудад хялбар хандах навигацийн товчлууруудаар хангадаг.
 */

package com.example.expensetrackerhomemade;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class ReportPage extends Application {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ExpenseTracker";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Report");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #ffffff;");

        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);
        menu.setStyle("-fx-background-color: #ffffff;");

        Button profileTab = new Button("Profile");
        profileTab.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        profileTab.getStyleClass().add("floating-tab-button");

        Button reportTab = new Button("Report");
        reportTab.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        reportTab.getStyleClass().add("floating-tab-button");

        Button budgetTab = new Button("Budget");
        budgetTab.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        budgetTab.getStyleClass().add("floating-tab-button");

        menu.getChildren().addAll(profileTab, reportTab, budgetTab);

        // Хамрах хугацаа тодорхойлох
        HBox timePeriodSection = new HBox(10);
        timePeriodSection.setAlignment(Pos.CENTER_LEFT);
        timePeriodSection.setPadding(new Insets(10));
        timePeriodSection.setStyle("-fx-background-color: #4CAF50;");

        Label timePeriodLabel = new Label("Enter time period:");
        timePeriodLabel.setTextFill(Color.WHITE);
        timePeriodLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        timePeriodSection.getChildren().addAll(timePeriodLabel, startDatePicker, new Label("to"), endDatePicker);

        // Report section
        VBox reportSection = new VBox(10);
        reportSection.setAlignment(Pos.CENTER_LEFT);
        reportSection.setPadding(new Insets(10));
        reportSection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc; -fx-border-width: 1px;");

        Label reportLabel = new Label("");
        reportLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        reportLabel.setWrapText(true);

        reportSection.getChildren().add(reportLabel);

        profileTab.setOnAction(event -> {
            System.out.println("Navigating to Profile Page...");
            ProfilePage profilePage = new ProfilePage();
            profilePage.start(new Stage());
            primaryStage.close();
        });

        budgetTab.setOnAction(event -> {
            System.out.println("Navigating to Budget Page...");
            BudgetPage budgetPage = new BudgetPage();
            budgetPage.start(new Stage());
            primaryStage.close();
        });

        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            if (startDate != null && endDate != null) {
                generateReport(startDate, endDate, reportLabel);
            } else {
                reportLabel.setText("Please select both start and end dates.");
            }
        });

        root.getChildren().addAll(menu, timePeriodSection, generateReportButton, reportSection);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateReport(LocalDate startDate, LocalDate endDate, Label reportLabel) {
        StringBuilder reportText = new StringBuilder();

        String expensesQuery = "SELECT SUM(amount) AS total_expenses FROM Transactions WHERE transaction_date BETWEEN ? AND ? AND name = 'Expense'";
        String earningsQuery = "SELECT SUM(amount) AS total_earnings FROM Transactions WHERE transaction_date BETWEEN ? AND ? AND name = 'Income'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement expensesPreparedStatement = connection.prepareStatement(expensesQuery);
             PreparedStatement earningsPreparedStatement = connection.prepareStatement(earningsQuery)) {

            expensesPreparedStatement.setDate(1, Date.valueOf(startDate));
            expensesPreparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = expensesPreparedStatement.executeQuery();
            if (resultSet.next()) {
                double totalExpenses = resultSet.getDouble("total_expenses");
                reportText.append("Total expenses: $").append(totalExpenses).append("\n\n");
            }

            earningsPreparedStatement.setDate(1, Date.valueOf(startDate));
            earningsPreparedStatement.setDate(2, Date.valueOf(endDate));
            resultSet = earningsPreparedStatement.executeQuery();
            if (resultSet.next()) {
                double totalEarnings = resultSet.getDouble("total_earnings");
                reportText.append("Total earnings: $").append(totalEarnings).append("\n\n");
            }

            String budgetQuery = "SELECT budget_amount, SUM(amount) AS total_expenses FROM Budgets b JOIN Transactions t ON t.transaction_date BETWEEN b.start_date AND b.end_date WHERE t.name = 'Expense' GROUP BY b.budget_amount";
            PreparedStatement budgetPreparedStatement = connection.prepareStatement(budgetQuery);
            resultSet = budgetPreparedStatement.executeQuery();
            if (resultSet.next()) {
                double budgetAmount = resultSet.getDouble("budget_amount");
                double totalExpenses = resultSet.getDouble("total_expenses");
                double remainingBudget = budgetAmount - totalExpenses;
                reportText.append("Budget status:\n");
                reportText.append("Budget amount: $").append(budgetAmount).append("\n");
                reportText.append("Remaining budget: $").append(remainingBudget).append("\n\n");
            }

            String categoriesQuery = "SELECT category, SUM(amount) AS total FROM Transactions WHERE transaction_date BETWEEN ? AND ? AND name = 'Expense' GROUP BY category ORDER BY total DESC LIMIT 3";
            PreparedStatement categoriesPreparedStatement = connection.prepareStatement(categoriesQuery);
            categoriesPreparedStatement.setDate(1, Date.valueOf(startDate));
            categoriesPreparedStatement.setDate(2, Date.valueOf(endDate));
            resultSet = categoriesPreparedStatement.executeQuery();
            reportText.append("Most spent categories:\n");
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                double totalAmount = resultSet.getDouble("total");
                reportText.append("- ").append(category).append(": $").append(totalAmount).append("\n");
            }

            String earningsSourcesQuery = "SELECT category, SUM(amount) AS total FROM Transactions WHERE transaction_date BETWEEN ? AND ? AND name = 'Income' GROUP BY category ORDER BY total DESC LIMIT 3";
            PreparedStatement earningsSourcesPreparedStatement = connection.prepareStatement(earningsSourcesQuery);
            earningsSourcesPreparedStatement.setDate(1, Date.valueOf(startDate));
            earningsSourcesPreparedStatement.setDate(2, Date.valueOf(endDate));
            resultSet = earningsSourcesPreparedStatement.executeQuery();
            reportText.append("\nMost earnings came from:\n");
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                double totalAmount = resultSet.getDouble("total");
                reportText.append("- ").append(category).append(": $").append(totalAmount).append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            reportText.append("Error generating report.");
        }
        reportLabel.setText(reportText.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}