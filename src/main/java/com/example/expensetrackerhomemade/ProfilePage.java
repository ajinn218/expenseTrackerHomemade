/**
 * @author Sortagjin
 * Хэрэглэгчийн профайлын мэдээлэл болон гүйлгээний түүхийг харуулдаг.
 * Хэрэглэгчдэд одоогийн үлдэгдэл, төсвийн байдал, мэдэгдлийг харах боломжийг олгодог.
 * Тайлан, Төсөв гэх мэт бусад хуудсуудад хялбар хандах навигацийн товчлууруудаар хангадаг.
 */

package com.example.expensetrackerhomemade;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class ProfilePage extends Application {
    // Гүйлгээнүүдийг энд хадгалах
    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    private static final String DB_URL = "jdbc:mysql://localhost:3306/ExpenseTracker";
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = "your_password"; // Replace with your database password

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profile Page");

        // Өмнөх гүйлгээнүүдийг татаж авах
        loadTransactions();

        // Үндсэн layout-ыг үүсгэх
        VBox root = createRootLayout();
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
    }

    // Үндсэн layout-ыг үүсгэх функц
    private VBox createRootLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #ffffff;");

        root.getChildren().addAll(
                createMenu(),
                createBalanceSection(),
                createLabel("Budget Status: Within Budget", "#388E3C"),
                createLabel("Notifications: Budget is more than 50% gone", "#4CAF50"),
                createTransactionListView(),
                createButtonsSection()
        );
        return root;
    }

    // Inside createMenu() method
    // Цэс үүсгэх функц
    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setAlignment(Pos.TOP_LEFT);

        Button profileTab = createTabButton("Profile", event -> System.out.println("Navigating to Profile Page..."));
        Button reportTab = createTabButton("Report", event -> {
            System.out.println("Navigating to Report Page...");
            ReportPage reportPage = new ReportPage();
            reportPage.start(new Stage());
        });
        Button budgetTab = createTabButton("Budget", event -> {
            System.out.println("Navigating to Budget Page...");
            BudgetPage budgetPage = new BudgetPage();
            budgetPage.start(new Stage());
        });

        menu.getChildren().addAll(profileTab, reportTab, budgetTab);
        return menu;
    }


    // Цэсний товчлуурыг үүсгэх функц
    private Button createTabButton(String text, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        button.setOnAction(eventHandler);
        return button;
    }

    // Хуримтлагдсан балансыг харуулах хэсгийг үүсгэх
    private HBox createBalanceSection() {
        HBox balanceSection = new HBox(10);
        balanceSection.setAlignment(Pos.CENTER_LEFT);
        balanceSection.setPadding(new Insets(10));
        balanceSection.setStyle("-fx-background-color: #388E3C;");

        Label balanceLabel = new Label("Balance: $1000.00");
        balanceLabel.setTextFill(Color.WHITE);
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button hideButton = new Button("Hide");
        hideButton.setStyle("-fx-background-color: #4CAF50;");
        hideButton.setOnAction(event -> balanceLabel.setText("Balance: ---"));

        balanceSection.getChildren().addAll(balanceLabel, hideButton);
        return balanceSection;
    }

    // Тэмдэгт үүсгэх функц
    private Label createLabel(String text, String backgroundColor) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setPadding(new Insets(10));
        label.setStyle("-fx-background-color: " + backgroundColor + ";");
        return label;
    }

    // Гүйлгээнүүдийг харуулах ListView үүсгэх
    private ScrollPane createTransactionListView() {
        ListView<Transaction> transactionListView = new ListView<>(transactions);
        transactionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Transaction transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (empty || transaction == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.format("$%.2f, %s, %s", transaction.getAmount(), transaction.getDate(), transaction.getCategory()));
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(event -> {
                        getListView().getItems().remove(transaction);
                        deleteTransaction(transaction);
                    });
                    setGraphic(deleteButton);
                }
            }
        });

        ScrollPane scrollPane = new ScrollPane(transactionListView);
        scrollPane.setPrefSize(300, 200);
        return scrollPane;
    }

    // Орлого болон зарлагын товчлуурыг үүсгэх
    private HBox createButtonsSection() {
        HBox buttonsSection = new HBox(10);
        buttonsSection.setAlignment(Pos.CENTER_LEFT);
        buttonsSection.setPadding(new Insets(10));
        buttonsSection.setStyle("-fx-background-color: #4CAF50;");

        Button addIncomeButton = createActionButton("+", event -> openTransactionDialog("Add Income", "Amount:"));
        Button addExpenseButton = createActionButton("-", event -> openTransactionDialog("Add Expense", "Amount:"));

        buttonsSection.getChildren().addAll(addIncomeButton, addExpenseButton);
        return buttonsSection;
    }

    // Товчлуурыг үүсгэх функц
    private Button createActionButton(String text, EventHandler<ActionEvent> eventHandler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #388E3C; -fx-text-fill: white; -fx-font-size: 16px;");
        button.setOnAction(eventHandler);
        return button;
    }

    // Гүйлгээ нэмэх цонхыг үүсгэх
    // Гүйлгээ нэмэх цонхыг үүсгэх функц
    private void openTransactionDialog(String title, String fieldLabel) {
        Dialog<Transaction> dialog = createTransactionDialog(title, fieldLabel);
        DatePicker datePicker = new DatePicker();
        GridPane grid = (GridPane) dialog.getDialogPane().getContent();
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);

        dialog.showAndWait().ifPresent(transaction -> {
            transaction.setDate(datePicker.getValue());
            transactions.add(transaction);
            insertTransaction(transaction);
        });
    }

    // Гүйлгээ нэмэх цонхыг үүсгэх функц
    private Dialog<Transaction> createTransactionDialog(String title, String fieldLabel) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle(title);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        amountField.setPromptText(fieldLabel);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        grid.add(new Label(fieldLabel), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                double amount = Double.parseDouble(amountField.getText());
                String category = categoryField.getText();
                return new Transaction(amount, LocalDate.now(), category);
            }
            return null;
        });

        return dialog;
    }

    // Өмнөх гүйлгээнүүдийг татаж авах функц
    private void loadTransactions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT amount, transaction_date AS date, category FROM Transactions"; // Adjusted column name
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                double amount = rs.getDouble("amount");
                LocalDate date = rs.getDate("date").toLocalDate(); // Changed to "date"
                String category = rs.getString("category");
                transactions.add(new Transaction(amount, date, category));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Гүйлгээг хадгалах функц
    private void insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO Transactions (amount, transaction_date, category) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setDate(2, Date.valueOf(transaction.getDate()));
            pstmt.setString(3, transaction.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Гүйлгээ устгах функц
    private void deleteTransaction(Transaction transaction) {
        String sql = "DELETE FROM Transactions WHERE amount = ? AND transaction_date = ? AND category = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setDate(2, Date.valueOf(transaction.getDate()));
            pstmt.setString(3, transaction.getCategory());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}