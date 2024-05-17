/**
 * @author Sortagjin
 * Хэрэглэгчдэд тодорхой ангилал, цаг хугацааны төсвийн хязгаарыг тогтоохыг зөвшөөрдөг.
 * Нийт зардал болон үлдсэн төсвийг багтаасан төсвийн байдлын хураангуйг харуулав.
 * Төсвийн оруулгуудыг нэмэх, засварлах, устгах сонголтуудыг санал болгодог.
 * Профайл, тайлан гэх мэт бусад хуудас руу шилжих боломжийг олгодог.
 */

package com.example.expensetrackerhomemade;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BudgetPage extends Application {
    private double budgetAmount; // Budget amount
    private ProfilePage profilePage; // Reference to ProfilePage to get budget amount

    private static final String strDBUrl = "jdbc:mysql://localhost:3306/ExpenseTracker";
    private static final String strDbUser = "root";
    private static final String strDbPassword = "your_password";

    private ObservableList<String> spendingHistory = FXCollections.observableArrayList();
    private Label remainingBudgetLabel; // Declare remainingBudgetLabel here

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Budget");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #ffffff;");

        // Menu section
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

        // Budget remaining section
        HBox budgetRemainingSection = new HBox(10);
        budgetRemainingSection.setAlignment(Pos.CENTER_LEFT);
        budgetRemainingSection.setPadding(new Insets(10));
        budgetRemainingSection.setStyle("-fx-background-color: #4CAF50;");

        Label budgetLabel = new Label("Budget status:");
        budgetLabel.setTextFill(Color.WHITE);
        budgetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));


        ProgressBar budgetProgressBar = new ProgressBar(0.8); // 80% remaining
        budgetProgressBar.setPrefWidth(200);

        remainingBudgetLabel = new Label(); // Initialize remainingBudgetLabel here
        remainingBudgetLabel.setTextFill(Color.WHITE);
        remainingBudgetLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        budgetRemainingSection.getChildren().addAll(budgetLabel, budgetProgressBar, remainingBudgetLabel);

        // Set up event handlers for menu buttons
        profileTab.setOnAction(event -> {
            // Navigate to ProfilePage
            ProfilePage profilePage = new ProfilePage();
            Stage profileStage = new Stage();
            profilePage.start(profileStage);
        });

        reportTab.setOnAction(event -> {
            // Navigate to ReportPage
            ReportPage reportPage = new ReportPage();
            Stage reportStage = new Stage();
            reportPage.start(reportStage);
        });

        // Reset budget section
        VBox resetBudgetSection = new VBox(10);
        resetBudgetSection.setAlignment(Pos.CENTER_LEFT);
        resetBudgetSection.setPadding(new Insets(10));
        resetBudgetSection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc; -fx-border-width: 1px;");

        Label resetBudgetLabel = new Label("Reset budget:");

        TextField budgetAmountField = new TextField();
        budgetAmountField.setPromptText("Enter budget amount");

        TextField spendingPeriodField = new TextField();
        spendingPeriodField.setPromptText("Enter spending period (days)");

        Button resetBudgetButton = new Button("Reset");
        resetBudgetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

        resetBudgetSection.getChildren().addAll(resetBudgetLabel, budgetAmountField, spendingPeriodField, resetBudgetButton);

        // Spending history section
        HBox spendingHistorySection = new HBox(10);
        spendingHistorySection.setAlignment(Pos.CENTER_LEFT);
        spendingHistorySection.setPadding(new Insets(10));
        spendingHistorySection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ccc; -fx-border-width: 1px;");

        ListView<String> spendingListView = new ListView<>();
        spendingListView.setItems(spendingHistory);

        spendingHistorySection.getChildren().add(spendingListView);
        // Add all sections to the root layout
        root.getChildren().addAll(menu, budgetRemainingSection, resetBudgetSection, spendingHistorySection);

        // Add a DatePicker to the UI
        DatePicker datePicker = new DatePicker();
        root.getChildren().add(datePicker);

        resetBudgetButton.setOnAction(event -> {
            // Get the budget amount and spending period from user inputs
            double newBudgetAmount = Double.parseDouble(budgetAmountField.getText());
            int newSpendingPeriod = Integer.parseInt(spendingPeriodField.getText());

            // Reset the budget to its initial amount
            budgetProgressBar.setProgress(1.0);
            remainingBudgetLabel.setText("$" + String.format("%.2f", newBudgetAmount) + "left out of $" + String.format("%.2f", budgetAmount));
            // Insert the new budget into the database
            insertBudget(newBudgetAmount, newSpendingPeriod);
        });

        // Set up scene and stage
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to set the budget amount
    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
        updateBudgetUI(); // Update the UI with the new budget amount
    }

    // Method to update UI with new budget amount
    private void updateBudgetUI() {
        remainingBudgetLabel.setText("$" + String.format("%.2f", budgetAmount) + " left out of $" + String.format("%.2f", budgetAmount));
    }

    private void insertBudget(double budgetAmount, int spendingPeriod) {
        String query = "INSERT INTO Budgets (budget_amount, spending_period) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(strDBUrl, strDbUser, strDbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setDouble(1, budgetAmount);
            preparedStatement.setInt(2, spendingPeriod);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Budget inserted successfully.");
            } else {
                System.out.println("Failed to insert budget.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
