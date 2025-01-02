package me.program;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

public class MainClass extends Application {

    private ArrayList<Double> priceList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("加密貨幣持倉計算器");

        // 圖片網址
        String btcLogoUrl = "https://cryptologos.cc/logos/bitcoin-btc-logo.png"; // 請替換為實際的BTC圖片網址
        String ethLogoUrl = "https://cryptologos.cc/logos/ethereum-eth-logo.png"; // 請替換為實際的ETH圖片網址

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // 標題圖片
        ImageView btcImageView = new ImageView(new Image(btcLogoUrl));
        btcImageView.setFitHeight(50);
        btcImageView.setFitWidth(50);
        GridPane.setConstraints(btcImageView, 0, 0);

        ImageView ethImageView = new ImageView(new Image(ethLogoUrl));
        ethImageView.setFitHeight(50);
        ethImageView.setFitWidth(50);
        GridPane.setConstraints(ethImageView, 1, 0);

        // Input fields with dialog for explanations
        Label accountBalanceLabel = createLabelWithDialog("帳戶餘額 (USD):", "輸入您交易帳戶的總資金。");
        GridPane.setConstraints(accountBalanceLabel, 0, 1);
        TextField accountBalanceField = new TextField();
        GridPane.setConstraints(accountBalanceField, 1, 1);

        Label riskLabel = createLabelWithDialog("風險百分比 (%):", "設定您每次交易願意承擔的風險百分比，例如 1%。");
        GridPane.setConstraints(riskLabel, 0, 2);
        TextField riskField = new TextField();
        GridPane.setConstraints(riskField, 1, 2);

        Label stopLossLabel = createLabelWithDialog("止損金額 (USD):", "輸入每單位止損範圍金額，例如每單位損失 10 USD。");
        GridPane.setConstraints(stopLossLabel, 0, 3);
        TextField stopLossField = new TextField();
        GridPane.setConstraints(stopLossField, 1, 3);

        Label cryptoLabel = createLabelWithDialog("加密貨幣 (BTCUSDT/ETHUSDT):", "輸入交易的加密貨幣，例如 BTCUSDT 或 ETHUSDT。");
        GridPane.setConstraints(cryptoLabel, 0, 4);
        TextField cryptoField = new TextField();
        GridPane.setConstraints(cryptoField, 1, 4);

        Label leverageLabel = createLabelWithDialog("槓桿倍數:", "輸入交易的槓桿倍數，例如 10x、20x 等。");
        GridPane.setConstraints(leverageLabel, 0, 5);
        TextField leverageField = new TextField();
        GridPane.setConstraints(leverageField, 1, 5);

        // Label to display the current price
        Label currentPriceLabel = new Label("目前價格 (USD):");
        GridPane.setConstraints(currentPriceLabel, 0, 6);
        Label currentPriceValueLabel = new Label("0.0"); // Initialize with 0.0
        GridPane.setConstraints(currentPriceValueLabel, 1, 6);

        // Label to display the suggested stop-loss price
        Label stopPriceSuggestionLabel = new Label("建議止損價格: N/A");
        GridPane.setConstraints(stopPriceSuggestionLabel, 0, 7);
        GridPane.setColumnSpan(stopPriceSuggestionLabel, 2);

        Button calculateButton = new Button("計算");
        GridPane.setConstraints(calculateButton, 1, 8);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 9);

        // Add action to calculate button
        calculateButton.setOnAction(event -> {
            try {
                double accountBalance = Double.parseDouble(accountBalanceField.getText());
                double riskPercentage = Double.parseDouble(riskField.getText());
                double stopLoss = Double.parseDouble(stopLossField.getText());
                double leverage = Double.parseDouble(leverageField.getText());
                String crypto = cryptoField.getText().toUpperCase();

                // Get current price and calculate stop-loss price
                double currentPrice = GetApi.fetchCryptoPrice(crypto );
                double stopPrice = crypto.equals("BTCUSDT") ? currentPrice - (currentPrice * 0.01) : crypto.equals("ETHUSDT") ? currentPrice - (currentPrice * (1.0/35.0)) : currentPrice;

                // Calculate position size

                double positionSize = (accountBalance * riskPercentage / 100) / stopLoss * leverage;
                resultLabel.setText(String.format("持倉大小: %.2f USD", positionSize));

                // Log data to a text file
                logDataToFile(currentPrice, (float)stopPrice,positionSize,(int)leverage);
            } catch (Exception e) {
                resultLabel.setText("錯誤: 無效的輸入或計算失敗");
                e.printStackTrace();
            }
        });

        // Timeline to update the current price every second based on user input
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String crypto = cryptoField.getText().toUpperCase();
            if (!crypto.isEmpty()) {
                double currentPrice = GetApi.fetchCryptoPrice(crypto);
                currentPriceValueLabel.setText(String.valueOf(currentPrice));
                priceList.add(currentPrice);

                // Calculate suggested stop-loss price based on input
                double stopLoss = crypto.equals("BTCUSDT") ? currentPrice * 0.01 : crypto.equals("ETHUSDT") ? currentPrice * (1.0/35.0) : 0.0; // 1% of BTC (1000 points), 1% of ETH (35 points)
                stopPriceSuggestionLabel.setText("建議止損價格: " + String.format("%.2f", currentPrice - stopLoss));

                // Log prices to a text file
                logPricesToFile();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Add components to grid
        grid.getChildren().addAll(
                btcImageView, ethImageView, accountBalanceLabel, accountBalanceField,
                riskLabel, riskField, stopLossLabel, stopLossField, cryptoLabel,
                cryptoField, leverageLabel, leverageField, currentPriceLabel,
                currentPriceValueLabel, stopPriceSuggestionLabel, calculateButton, resultLabel
        );

        // Set scene and stage
        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void logPricesToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("prices_log.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("價格: " + priceList.get(priceList.size() - 1) + " USD\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logDataToFile(double currentPrice, double stopPrice, double positionSize,int leverage) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("position_log.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("目前價格: " + currentPrice + " USD\n");
            writer.write("止損價格: " + stopPrice + " USD\n");
            writer.write("持倉大小: " + positionSize + " USD\n");
            writer.write("槓桿倍數: " + leverage + "x\n");
            writer.write("---------------------\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Label createLabelWithDialog(String labelText, String dialogMessage) {
        Label label = new Label(labelText);
        label.setOnMouseClicked(event -> {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("說明");
            dialog.setHeaderText(labelText);
            dialog.setContentText(dialogMessage);
            dialog.showAndWait();
        });
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
