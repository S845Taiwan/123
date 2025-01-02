package me.program;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class GetApi {
    // Method must be inside the class
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
            return jsonResponse.getDouble("price");
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Indicate an error
        }
    }
}
