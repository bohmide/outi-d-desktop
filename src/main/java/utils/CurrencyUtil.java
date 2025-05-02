package utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyUtil {
    private static final String EXCHANGE_RATE_API = "https://api.exchangerate-api.com/v4/latest/TND";
    private static final String RATE_FILE_PATH = "src/main/resources/exchange_rates.json";
    private static Map<String, Double> exchangeRates = new HashMap<>();

    static {

        fetchLiveExchangeRates(); // This also saves to file
        exchangeRates.put("USD", 0.34);
        exchangeRates.put("GBP", 0.27);
        exchangeRates.put("CAD", 0.46);
        exchangeRates.put("JPY", 38.0);

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
            // If locale is unknown, try to load exchange rates from file
            loadExchangeRatesFromFile();
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

            for (String currency : rates.keySet()) {
                exchangeRates.put(currency, rates.getDouble(currency));
            }

            System.out.println("Successfully fetched live exchange rates");
            saveExchangeRatesToFile(); // Save rates to file after fetching
        } catch (Exception e) {
            System.err.println("Failed to fetch live exchange rates. Using default or cached rates.");
            loadExchangeRatesFromFile(); // Try loading from file
        }
    }

    private static void saveExchangeRatesToFile() {
        try (FileWriter writer = new FileWriter(RATE_FILE_PATH)) {
            JSONObject json = new JSONObject(exchangeRates);
            writer.write(json.toString(4)); // Pretty print
            System.out.println("Exchange rates saved to file.");
        } catch (IOException e) {
            System.err.println("Failed to save exchange rates to file.");
        }
    }

    private static void loadExchangeRatesFromFile() {
        try (Scanner scanner = new Scanner(new File(RATE_FILE_PATH))) {
            StringBuilder jsonBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonBuilder.append(scanner.nextLine());
            }
            JSONObject json = new JSONObject(jsonBuilder.toString());
            for (String currency : json.keySet()) {
                exchangeRates.put(currency, json.getDouble(currency));
            }
            System.out.println("Exchange rates loaded from file.");
        } catch (IOException e) {
            System.err.println("Could not load exchange rates from file.");
        }
    }

    public static double convertFromTND(double amount, Currency targetCurrency) {
        String currencyCode = targetCurrency.getCurrencyCode();
        double rate = exchangeRates.getOrDefault(currencyCode, 1.0);
        return amount * rate;
    }
}
