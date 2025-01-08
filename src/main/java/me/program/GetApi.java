package me.program;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONObject;

public class GetApi {
    // 儲存價格日誌的 ArrayList
    private static ArrayList<Double> priceLog = new ArrayList<>();

    // 根據加密貨幣代碼獲取當前價格
    public static double fetchCryptoPrice(String crypto) {
        // Binance API 的 URL，根據加密貨幣代碼獲取價格
        String apiUrl = "https://api.binance.com/api/v3/ticker/price?symbol=" + crypto;
        StringBuilder result = new StringBuilder();

        try {
            // 使用給定的 API URL 創建 URL 物件
            URL url = new URL(apiUrl);
            // 打開與該 URL 的連接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 設置請求方法為 GET
            conn.setRequestMethod("GET");

            // 讀取 API 返回的數據
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                // 持續讀取返回的每一行
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

            // 解析返回的 JSON 響應
            JSONObject jsonResponse = new JSONObject(result.toString());
            // 提取 JSON 中的價格數據
            double price = jsonResponse.getDouble("price");

            // 將價格記錄到日誌中
            logPrice(price);
            return price;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 出現錯誤時返回 -1
        }
    }

    // 記錄價格到日誌中
    private static void logPrice(double price) {
        // 將價格添加到 ArrayList 中
        priceLog.add(price);

        // 將價格追加寫入日誌文件
        try (FileWriter writer = new FileWriter("price_log.txt", true)) {
            writer.write(price + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 獲取價格日誌
    public static ArrayList<Double> getPriceLog() {
        return priceLog;
    }
}
