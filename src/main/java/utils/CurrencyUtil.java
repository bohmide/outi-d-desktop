package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;

public class CurrencyUtil {

    public static Currency getCurrencyFromIP() {
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000); // 3 secondes max pour éviter de bloquer l'app
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
            } else {
                System.err.println("No country code found in response. Falling back to system locale.");
                return getCurrencyFromSystemLocale();
            }

        } catch (Exception e) {
            System.err.println("No internet connection. Using system region to detect currency.");
            return getCurrencyFromSystemLocale();
        }
    }

    private static Currency getCurrencyFromSystemLocale() {
        Locale defaultLocale = Locale.getDefault();
        try {
            return Currency.getInstance(defaultLocale);
        } catch (IllegalArgumentException e) {
            System.err.println("System locale has invalid country code. Defaulting to USD.");
            return Currency.getInstance("USD");
        }
    }

    private static String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return json.substring(startIndex, endIndex);
    }
}
