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

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class MainClass extends Application {

    private ArrayList<Double> priceList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("加密貨幣持倉計算器");

        // 圖片網址
        String btcLogoUrl = "https://cryptologos.cc/logos/bitcoin-btc-logo.png";
        String ethLogoUrl = "https://cryptologos.cc/logos/ethereum-eth-logo.png";

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

        Label riskAmountLabel = createLabelWithDialog("每單止損金額 (USD):", "輸入每單交易您願意承擔的虧損金額，例如 100 USD。");
        GridPane.setConstraints(riskAmountLabel, 0, 2);
        TextField riskAmountField = new TextField();
        GridPane.setConstraints(riskAmountField, 1, 2);

        Label cryptoLabel = new Label("加密貨幣:");
        GridPane.setConstraints(cryptoLabel, 0, 3);
        ComboBox<String> cryptoComboBox = new ComboBox<>();
        cryptoComboBox.getItems().addAll("BTCUSDT", "ETHUSDT");
        cryptoComboBox.setValue("BTCUSDT");
        GridPane.setConstraints(cryptoComboBox, 1, 3);

        Label leverageLabel = createLabelWithDialog("槓桿倍數:", "輸入交易的槓桿倍數，例如 10x、20x 等。");
        GridPane.setConstraints(leverageLabel, 0, 4);
        TextField leverageField = new TextField();
        GridPane.setConstraints(leverageField, 1, 4);

        Label currentPriceLabel = new Label("目前價格 (USD):");
        GridPane.setConstraints(currentPriceLabel, 0, 5);
        Label currentPriceValueLabel = new Label("0.0"); // Initialize with 0.0
        GridPane.setConstraints(currentPriceValueLabel, 1, 5);

        Label stopPriceSuggestionLabel = new Label("建議止損價格: N/A");
        GridPane.setConstraints(stopPriceSuggestionLabel, 0, 6);
        GridPane.setColumnSpan(stopPriceSuggestionLabel, 2);

        Button calculateButton = new Button("計算");
        GridPane.setConstraints(calculateButton, 1, 7);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 8);

        calculateButton.setOnAction(event -> {
            try {
                double accountBalance = Double.parseDouble(accountBalanceField.getText());
                double riskAmount = Double.parseDouble(riskAmountField.getText());
                double leverage = Double.parseDouble(leverageField.getText());
                String crypto = cryptoComboBox.getValue();

                double currentPrice = GetApi.fetchCryptoPrice(crypto);
                double positionSize = riskAmount * leverage / currentPrice;
                double margin = riskAmount / leverage;
                double stopPrice = currentPrice - (riskAmount / positionSize);
                double liquidationPrice = currentPrice * (1 - (1 / leverage));

                resultLabel.setText(String.format("每單止損金額: %.2f USD\n持倉大小: %.6f %s\n保證金: %.2f USD\n止損價格: %.2f USD\n強平價格: %.2f USD",
                        riskAmount, positionSize, crypto, margin, stopPrice, liquidationPrice));

                logDataToFile(currentPrice, stopPrice, positionSize, margin, (int) leverage, liquidationPrice);
            } catch (Exception e) {
                resultLabel.setText("錯誤: 無效的輸入或計算失敗");
                e.printStackTrace();
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String crypto = cryptoComboBox.getValue();
            double currentPrice = GetApi.fetchCryptoPrice(crypto);
            currentPriceValueLabel.setText(String.valueOf(currentPrice));
            priceList.add(currentPrice);

            double stopLoss = riskAmountField.getText().isEmpty() ? 0 : Double.parseDouble(riskAmountField.getText());
            double suggestedStopPrice = currentPrice - (stopLoss / currentPrice);
            stopPriceSuggestionLabel.setText("建議止損價格: " + String.format("%.2f", suggestedStopPrice));

            logPricesToFile();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        grid.getChildren().addAll(
                btcImageView, ethImageView, accountBalanceLabel, accountBalanceField,
                riskAmountLabel, riskAmountField, cryptoLabel, cryptoComboBox, leverageLabel, leverageField,
                currentPriceLabel, currentPriceValueLabel, stopPriceSuggestionLabel, calculateButton, resultLabel
        );

        Scene scene = new Scene(grid, 600, 500);
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

    private void logDataToFile(double currentPrice, double stopPrice, double positionSize, double margin, int leverage, double liquidationPrice) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("position_log.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("目前價格: " + currentPrice + " USD\n");
            writer.write("止損價格: " + stopPrice + " USD\n");
            writer.write("持倉大小: " + positionSize + " USD\n");
            writer.write("保證金: " + margin + " USD\n");
            writer.write("槓桿倍數: " + leverage + "x\n");
            writer.write("強平價格: " + liquidationPrice + " USD\n");
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
