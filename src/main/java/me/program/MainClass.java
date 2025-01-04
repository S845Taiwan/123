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

import java.util.ArrayList;

public class MainClass extends Application {

    private ArrayList<Double> priceList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("加密貨幣持倉計算器");

        // 圖片網址
        String btcLogoUrl = "https://cryptologos.cc/logos/bitcoin-btc-logo.png";
        String ethLogoUrl = "https://cryptologos.cc/logos/ethereum-eth-logo.png";

        // GridPane 佈局
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

        // 輸入欄位
        Label accountBalanceLabel = new Label("帳戶餘額 (USD):");
        GridPane.setConstraints(accountBalanceLabel, 0, 1);
        TextField accountBalanceField = new TextField();
        GridPane.setConstraints(accountBalanceField, 1, 1);

        Label riskModeLabel = new Label("風險模式:");
        GridPane.setConstraints(riskModeLabel, 0, 2);
        ComboBox<String> riskModeComboBox = new ComboBox<>();
        riskModeComboBox.getItems().addAll("百分比 (%)", "固定金額 (USD)");
        riskModeComboBox.setValue("百分比 (%)");
        GridPane.setConstraints(riskModeComboBox, 1, 2);

        Label riskValueLabel = new Label("風險百分比 (%):");
        GridPane.setConstraints(riskValueLabel, 0, 3);
        TextField riskValueField = new TextField();
        GridPane.setConstraints(riskValueField, 1, 3);

        riskModeComboBox.setOnAction(event -> {
            if (riskModeComboBox.getValue().equals("百分比 (%)")) {
                riskValueLabel.setText("風險百分比 (%):");
            } else {
                riskValueLabel.setText("風險金額 (USD):");
            }
        });

        Label stopLossLabel = new Label("止損範圍 (USD):");
        GridPane.setConstraints(stopLossLabel, 0, 4);
        TextField stopLossField = new TextField();
        GridPane.setConstraints(stopLossField, 1, 4);

        Label cryptoLabel = new Label("加密貨幣 (BTCUSDT/ETHUSDT):");
        GridPane.setConstraints(cryptoLabel, 0, 5);
        TextField cryptoField = new TextField();
        GridPane.setConstraints(cryptoField, 1, 5);

        Label leverageLabel = new Label("槓桿倍數:");
        GridPane.setConstraints(leverageLabel, 0, 6);
        TextField leverageField = new TextField();
        GridPane.setConstraints(leverageField, 1, 6);

        Label currentPriceLabel = new Label("目前價格 (USD):");
        GridPane.setConstraints(currentPriceLabel, 0, 7);
        Label currentPriceValueLabel = new Label("0.0");
        GridPane.setConstraints(currentPriceValueLabel, 1, 7);

        Button calculateButton = new Button("計算");
        GridPane.setConstraints(calculateButton, 1, 8);

        Label resultLabel = new Label();
        GridPane.setConstraints(resultLabel, 1, 9);

        calculateButton.setOnAction(event -> {
            try {
                double accountBalance = Double.parseDouble(accountBalanceField.getText());
                double stopLoss = Double.parseDouble(stopLossField.getText());
                double leverage = Double.parseDouble(leverageField.getText());
                String crypto = cryptoField.getText().toUpperCase();

                double riskAmount;
                if (riskModeComboBox.getValue().equals("百分比 (%)")) {
                    double riskPercentage = Double.parseDouble(riskValueField.getText());
                    riskAmount = accountBalance * (riskPercentage / 100);
                } else {
                    riskAmount = Double.parseDouble(riskValueField.getText());
                }

                double currentPrice = GetApi.fetchCryptoPrice(crypto);
                double positionSize = riskAmount / stopLoss * leverage;
                double margin = positionSize / leverage;
                double liquidationPrice = currentPrice * (1 - (1 / leverage));

                resultLabel.setText(String.format(
                        "風險金額: %.2f USD\n持倉大小: %.2f USD\n保證金: %.2f USD\n強平價格: %.2f USD",
                        riskAmount, positionSize, margin, liquidationPrice
                ));
            } catch (Exception e) {
                resultLabel.setText("錯誤: 無效的輸入或計算失敗");
                e.printStackTrace();
            }
        });

        grid.getChildren().addAll(
                btcImageView, ethImageView, accountBalanceLabel, accountBalanceField,
                riskModeLabel, riskModeComboBox, riskValueLabel, riskValueField,
                stopLossLabel, stopLossField, cryptoLabel, cryptoField,
                leverageLabel, leverageField, currentPriceLabel, currentPriceValueLabel,
                calculateButton, resultLabel
        );

        Scene scene = new Scene(grid, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
