/**
 * @author Sortagjin
 * Пин код оруулах цонх үүсгэдэг
 * Пин код зөв оруулсан тохиолдолд профайл хуудас руу шилжинэ
 * Буруу пин код оруулсан тохиолдолд анхааруулга өгөх болно
 */

package com.example.expensetrackerhomemade;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PinCodePage extends Application {

    // Пин кодыг энд тодорхойлсон
    private String pin = "1234";
    private StringBuilder enteredPin = new StringBuilder();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Pin Code Page");

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setStyle("-fx-background-color: #f0f0f0;"); // Цонхонд өнгө өгөх

        Label pinLabel = new Label("Enter Pin:");
        grid.add(pinLabel, 0, 0);

        TextField pinField = new TextField();
        pinField.setEditable(false);
        grid.add(pinField, 1, 0);

        Button[] numBtns = new Button[10];
        for (int i = 0; i < numBtns.length; i++) {
            final int digit = i; // Тоо тус бүрийн утга бөглөх
            numBtns[i] = new Button(Integer.toString(i));
            numBtns[i].setMinSize(50, 50);
            numBtns[i].setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14;");
            numBtns[i].setOnAction(event -> {
                enteredPin.append(digit);
                pinField.setText(enteredPin.toString());
                if (enteredPin.length() == 4) {
                    checkPin(primaryStage);
                }
            });
        }

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14;");
        clearButton.setOnAction(event -> {
            enteredPin.setLength(0);
            pinField.clear();
        });

        grid.add(numBtns[1], 0, 1);
        grid.add(numBtns[2], 1, 1);
        grid.add(numBtns[3], 2, 1);
        grid.add(numBtns[4], 0, 2);
        grid.add(numBtns[5], 1, 2);
        grid.add(numBtns[6], 2, 2);
        grid.add(numBtns[7], 0, 3);
        grid.add(numBtns[8], 1, 3);
        grid.add(numBtns[9], 2, 3);
        grid.add(clearButton, 0, 4);

        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Пин код шалгах функц
     * Зөв пин код оруулсан бол профайл хуудас руу шилжинэ
     * Буруу пин код оруулсан бол анхааруулга өгнө
     */
    private void checkPin(Stage primaryStage) {
        if (enteredPin.toString().equals(pin)) {
            System.out.println("Pin correct! You're logged in.");
            // Профайл хуудас руу шилжинэ
            ProfilePage profilePage = new ProfilePage();
            try {
                profilePage.start(new Stage()); // Шинэ цонх нээх
                primaryStage.close(); // Пин код оруулах цонхыг хаах
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Incorrect pin. Please try again.");
            // Буруу пин код оруулсан тохиолдолд анхааруулга өгөх
            enteredPin.setLength(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}