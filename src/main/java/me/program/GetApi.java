package me.program;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONObject;

public class GetApi {
    private static ArrayList<Double> priceLog = new ArrayList<>();

    public static double fetchCryptoPrice(String crypto) {
        String apiUrl = "https://api.binance.com/api/v3/ticker/price?symbol=" + crypto;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(result.toString());
            double price = jsonResponse.getDouble("price");

            // Log the price to the ArrayList and file
            logPrice(price);
            return price;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Indicate an error
        }
    }

    private static void logPrice(double price) {
        // Add the price to the ArrayList
        priceLog.add(price);

        // Append the price to the log file
        try (FileWriter writer = new FileWriter("price_log.txt", true)) {
            writer.write(price + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Double> getPriceLog() {
        return priceLog;
    }
}
