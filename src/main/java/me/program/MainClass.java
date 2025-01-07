package me.program;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainClass extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("加密貨幣持倉計算器");

        // 動態背景設置
        StackPane root = new StackPane();
        Rectangle background = new Rectangle(600, 600, Color.LIGHTBLUE);
        root.getChildren().add(background);

        // 背景色漸變動畫
        FillTransition fillTransition = new FillTransition(Duration.seconds(5), background);
        fillTransition.setFromValue(Color.LIGHTBLUE);
        fillTransition.setToValue(Color.LIGHTPINK);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();

        // 圖片網址
        String btcLogoUrl = "https://cryptologos.cc/logos/bitcoin-btc-logo.png";
        String ethLogoUrl = "https://cryptologos.cc/logos/ethereum-eth-logo.png";
        String bnbLogoUrl = "https://cryptologos.cc/logos/binance-coin-bnb-logo.png";
        String xrpLogoUrl = "https://cryptologos.cc/logos/xrp-xrp-logo.png";
        String adaLogoUrl = "https://cryptologos.cc/logos/cardano-ada-logo.png";
        String dogeLogoUrl = "https://cryptologos.cc/logos/dogecoin-doge-logo.png";
        String solLogoUrl = "https://cryptologos.cc/logos/solana-sol-logo.png";
        String maticLogoUrl = "https://cryptologos.cc/logos/polygon-matic-logo.png";
        String dotLogoUrl = "https://cryptologos.cc/logos/polkadot-dot-logo.png";
        String ltcLogoUrl = "https://cryptologos.cc/logos/litecoin-ltc-logo.png";

        // GridPane layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);

        // 標題圖片
        ImageView cryptoImageView = new ImageView(new Image(btcLogoUrl));
        cryptoImageView.setFitHeight(50);
        cryptoImageView.setFitWidth(50);
        GridPane.setConstraints(cryptoImageView, 0, 0);

        Label titleLabel = new Label("加密貨幣持倉計算器");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(titleLabel, 1, 0, 2, 1);

        // Input fields
        Label accountBalanceLabel = new Label("帳戶餘額 (USD):");
        GridPane.setConstraints(accountBalanceLabel, 0, 1);
        TextField accountBalanceField = new TextField();
        GridPane.setConstraints(accountBalanceField, 1, 1);

        Label cryptoLabel = new Label("加密貨幣:");
        GridPane.setConstraints(cryptoLabel, 0, 2);

        // 加密貨幣選項列表
        ComboBox<String> cryptoComboBox = new ComboBox<>();
        cryptoComboBox.getItems().addAll(
                "BTC - Bitcoin",
                "ETH - Ethereum",
                "BNB - Binance Coin",
                "XRP - Ripple",
                "ADA - Cardano",
                "DOGE - Dogecoin",
                "SOL - Solana",
                "POL - Polygon",
                "DOT - Polkadot",
                "LTC - Litecoin"
        );
        cryptoComboBox.setValue("BTC - Bitcoin"); // 預設選擇 Bitcoin
        GridPane.setConstraints(cryptoComboBox, 1, 2);

        Label currentPriceLabel = new Label("目前價格 (USD):");
        GridPane.setConstraints(currentPriceLabel, 0, 3);
        Label currentPriceValueLabel = new Label("0.0"); // Initialize with 0.0
        GridPane.setConstraints(currentPriceValueLabel, 1, 3);

        Label orderTypeLabel = new Label("下單模式:");
        GridPane.setConstraints(orderTypeLabel, 0, 4);
        ComboBox<String> orderTypeComboBox = new ComboBox<>();
        orderTypeComboBox.getItems().addAll("市價單", "限價單");
        orderTypeComboBox.setValue("市價單"); // 預設為市價單
        GridPane.setConstraints(orderTypeComboBox, 1, 4);

        Label limitPriceLabel = new Label("限價價格 (USD):");
        GridPane.setConstraints(limitPriceLabel, 0, 5);
        TextField limitPriceField = new TextField();
        limitPriceField.setDisable(true); // 預設禁用
        GridPane.setConstraints(limitPriceField, 1, 5);

        // 動態切換限價輸入框的啟用狀態
        orderTypeComboBox.setOnAction(event -> {
            if (orderTypeComboBox.getValue().equals("限價單")) {
                limitPriceField.setDisable(false);
            } else {
                limitPriceField.setDisable(true);
                limitPriceField.clear();
            }
        });

        // 清空限價框並更新圖片當幣種變更時
        cryptoComboBox.setOnAction(event -> {
            limitPriceField.clear();
            String selectedCrypto = cryptoComboBox.getValue().split(" - ")[0];
            switch (selectedCrypto) {
                case "BTC":
                    cryptoImageView.setImage(new Image(btcLogoUrl));
                    break;
                case "ETH":
                    cryptoImageView.setImage(new Image(ethLogoUrl));
                    break;
                case "BNB":
                    cryptoImageView.setImage(new Image(bnbLogoUrl));
                    break;
                case "XRP":
                    cryptoImageView.setImage(new Image(xrpLogoUrl));
                    break;
                case "ADA":
                    cryptoImageView.setImage(new Image(adaLogoUrl));
                    break;
                case "DOGE":
                    cryptoImageView.setImage(new Image(dogeLogoUrl));
                    break;
                case "SOL":
                    cryptoImageView.setImage(new Image(solLogoUrl));
                    break;
                case "MATIC":
                    cryptoImageView.setImage(new Image(maticLogoUrl));
                    break;
                case "DOT":
                    cryptoImageView.setImage(new Image(dotLogoUrl));
                    break;
                case "LTC":
                    cryptoImageView.setImage(new Image(ltcLogoUrl));
                    break;
                default:
                    cryptoImageView.setImage(new Image("https://cryptologos.cc/logos/default-placeholder.png"));
            }
        });

        Label directionLabel = new Label("交易方向:");
        GridPane.setConstraints(directionLabel, 0, 6);
        ComboBox<String> directionComboBox = new ComboBox<>();
        directionComboBox.getItems().addAll("做多", "做空");
        directionComboBox.setValue("做多"); // 預設為做多
        GridPane.setConstraints(directionComboBox, 1, 6);

        Label stopLossAmountLabel = new Label("預期損失 (USD):");
        GridPane.setConstraints(stopLossAmountLabel, 0, 7);
        TextField stopLossAmountField = new TextField();
        GridPane.setConstraints(stopLossAmountField, 1, 7);

        Label leverageLabel = new Label("槓桿倍數:");
        GridPane.setConstraints(leverageLabel, 0, 8);
        TextField leverageField = new TextField();
        GridPane.setConstraints(leverageField, 1, 8);

        Label volatilityLabel = new Label("價格波動幅度 (%):");
        GridPane.setConstraints(volatilityLabel, 0, 9);
        TextField volatilityField = new TextField("2.0"); // 預設值 2%
        GridPane.setConstraints(volatilityField, 1, 9);

        Button calculateButton = new Button("計算");
        GridPane.setConstraints(calculateButton, 1, 10);

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-border-color: black; -fx-padding: 10px; -fx-background-color: #e0e0e0; -fx-font-size: 14px;");
        GridPane.setConstraints(resultLabel, 0, 11);
        GridPane.setColumnSpan(resultLabel, 2);

        calculateButton.setOnAction(event -> {
            try {
                double stopLossAmount = Double.parseDouble(stopLossAmountField.getText());
                double leverage = Double.parseDouble(leverageField.getText());
                double volatility = Double.parseDouble(volatilityField.getText()) / 100; // 百分比轉小數

                // 從 ComboBox 選取幣種代碼
                String selectedCrypto = cryptoComboBox.getValue().split(" - ")[0] + "USDT";
                double currentPrice = GetApi.fetchCryptoPrice(selectedCrypto);
                double priceToUse = currentPrice; // 默認使用市價

                if (orderTypeComboBox.getValue().equals("限價單")) {
                    if (limitPriceField.getText().isEmpty()) {
                        resultLabel.setText("錯誤: 請輸入限價價格");
                        return;
                    }
                    priceToUse = Double.parseDouble(limitPriceField.getText());
                }

                // 計算波動金額與止損價格
                double priceMovement = volatility * priceToUse; // 波動金額
                double stopPrice = directionComboBox.getValue().equals("做多")
                        ? priceToUse - priceMovement // 做多止損價格
                        : priceToUse + priceMovement; // 做空止損價格

                // 計算保證金
                double margin = stopLossAmount / (volatility * leverage);

                if (margin > Double.parseDouble(accountBalanceField.getText())) {
                    resultLabel.setText("錯誤: 保證金超過帳戶餘額");
                } else {
                    resultLabel.setText(String.format(
                            "幣種: %s\n保證金: %.2f USD\n止損價格: %.2f USD\n預期損失: %.2f USD",
                            selectedCrypto, margin, stopPrice, stopLossAmount
                    ));
                }

            } catch (Exception e) {
                resultLabel.setText("錯誤: 無效的輸入或計算失敗");
                e.printStackTrace();
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String selectedCrypto = cryptoComboBox.getValue().split(" - ")[0] + "USDT";
            if (!selectedCrypto.isEmpty()) {
                double currentPrice = GetApi.fetchCryptoPrice(selectedCrypto);
                currentPriceValueLabel.setText(String.valueOf(currentPrice));
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        grid.getChildren().addAll(
                cryptoImageView, titleLabel, accountBalanceLabel, accountBalanceField,
                cryptoLabel, cryptoComboBox, currentPriceLabel, currentPriceValueLabel,
                orderTypeLabel, orderTypeComboBox, limitPriceLabel, limitPriceField,
                directionLabel, directionComboBox, stopLossAmountLabel, stopLossAmountField,
                leverageLabel, leverageField, volatilityLabel, volatilityField,
                calculateButton, resultLabel
        );

        root.getChildren().add(grid);
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
