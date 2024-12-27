package me.program;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;


public class Main extends Application{
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Crypto Position Calculator");

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // Input fields
        Label accountBalanceLabel = new Label("Account Balance (USD):");
        GridPane.setConstraints(accountBalanceLabel, 0, 0);
        TextField accountBalanceField = new TextField();
        GridPane.setConstraints(accountBalanceField, 1, 0);

        Label riskLabel = new Label("Risk Percentage (%):");
        GridPane.setConstraints(riskLabel, 0, 1);
        TextField riskField = new TextField();
        GridPane.setConstraints(riskField, 1, 1);

        Label stopLossLabel = new Label("Stop Loss (USD):");
        GridPane.setConstraints(stopLossLabel, 0, 2);
        TextField stopLossField = new TextField();
        GridPane.setConstraints(stopLossField, 1, 2);

        Label cryptoLabel = new Label("Cryptocurrency (BTC/ETH):");
        GridPane.setConstraints(cryptoLabel, 0, 3);
        TextField cryptoField = new TextField();
        GridPane.setConstraints(cryptoField, 1, 3);

        Button calculateButton = new Button("Calculate");
        GridPane.setConstraints(calculateButton, 1, 4);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 5);

        // Add action to calculate button
        calculateButton.setOnAction(event -> {
            try {
                double accountBalance = Double.parseDouble(accountBalanceField.getText());
                double riskPercentage = Double.parseDouble(riskField.getText());
                double stopLoss = Double.parseDouble(stopLossField.getText());
                String crypto = cryptoField.getText().toLowerCase();

                // Fetch price using GetApi class
                String apiResponse = GetApi.fetchCryptoPrice(crypto);
                double price = Double.parseDouble(apiResponse.split(":")[2].replace("}", ""));

                // Calculate position size
                double positionSize = Calculator.calculatePositionSize(accountBalance, riskPercentage, stopLoss);

                // Display result
                resultLabel.setText(String.format("Position Size: %.2f USD\nCrypto Price: $%.2f", positionSize, price));
            } catch (Exception e) {
                resultLabel.setText("Error: Invalid input or API failure.");
                e.printStackTrace();
            }
        });

        // Add components to grid
        grid.getChildren().addAll(accountBalanceLabel, accountBalanceField, riskLabel, riskField,
                stopLossLabel, stopLossField, cryptoLabel, cryptoField, calculateButton, resultLabel);

        // Set scene and stage
        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);

    }
}
