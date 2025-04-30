package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;

public class CurrencyUtil {
    private static final String EXCHANGE_RATE_API = "https://api.exchangerate-api.com/v4/latest/TND";
    private static Map<String, Double> exchangeRates = new HashMap<>();

    static {
        // Initialize with default rates in case API fails
        exchangeRates.put("USD", 0.34);
        //exchangeRates.put("EUR", 0.31);
        exchangeRates.put("GBP", 0.27);
        exchangeRates.put("CAD", 0.46);
        exchangeRates.put("JPY", 38.0);

        // Try to fetch live rates
        fetchLiveExchangeRates();
    }

    public static Currency getCurrencyFromIP() {
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String json = response.toString();
            String countryCode = extractValue(json, "countryCode");

            if (countryCode != null && !countryCode.isEmpty()) {
                Locale locale = new Locale("", countryCode);
                return Currency.getInstance(locale);
            }
        } catch (Exception e) {
            System.err.println("Couldn't detect currency from IP. Using system locale.");
        }

        return getCurrencyFromSystemLocale();
    }

    private static Currency getCurrencyFromSystemLocale() {
        Locale defaultLocale = Locale.getDefault();
        try {
            return Currency.getInstance(defaultLocale);
        } catch (IllegalArgumentException e) {
            return Currency.getInstance("USD");
        }
    }

    private static String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        return json.substring(startIndex, endIndex);
    }

    private static void fetchLiveExchangeRates() {
        try {
            URL url = new URL(EXCHANGE_RATE_API);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("rates");

            // Update our exchange rates map
            for (String currency : rates.keySet()) {
                exchangeRates.put(currency, rates.getDouble(currency));
            }

            System.out.println("Successfully fetched live exchange rates");
        } catch (Exception e) {
            System.err.println("Failed to fetch live exchange rates. Using default rates.");
        }
    }

    public static double convertFromTND(double amount, Currency targetCurrency) {
        String currencyCode = targetCurrency.getCurrencyCode();
        double rate = exchangeRates.getOrDefault(currencyCode, 1.0);
        return amount * rate;
    }
}