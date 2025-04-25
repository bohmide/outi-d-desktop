package services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FreeAIImageService {
    private static final String API_KEY = "ZoDInBCsQz8dDr1YXMZOVQYNv22sGbHzUAx9KNqGsRMlfkH8Bg4ipFzVGkmZ";
    private static final String MODEL_ID = "stable-diffusion-v1-5";
    private static final String API_ENDPOINT = "https://modelslab.com/api/v6/images/text2img";

    public String generateCertificate(String badgeName, int score) {
        String prompt = String.format(
                "Digital certificate, award badge %s, score %d, modern flat design, golden stars, vector illustration",
                badgeName, score);

        String jsonBody = String.format(
                "{"
                        + "\"key\": \"%s\","
                        + "\"model_id\": \"%s\","
                        + "\"prompt\": \"%s\","
                        + "\"width\": 512,"
                        + "\"height\": 512,"
                        + "\"negative_prompt\": \"blurry, low quality\","
                        + "\"num_images\": 1"
                        + "}",
                API_KEY,
                MODEL_ID,
                prompt.replace("\"", "\\\"") // Échapper les guillemets
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Réponse brute: " + response.body()); // Debug
            return parseImageUrl(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String parseImageUrl(String jsonResponse) {
        try {
            JSONObject responseObj = new JSONObject(jsonResponse);

            // Cas 1: Génération en cours
            if (responseObj.getString("status").equals("processing")) {
                String fetchUrl = responseObj.getString("fetch_result");
                double eta = responseObj.getDouble("eta");

                System.out.println("Génération en cours... Nouvelle tentative dans " + eta + " secondes");
                Thread.sleep((long) (eta * 1000L));

                // Ajouter la logique de réessai
                return fetchGeneratedImage(fetchUrl);
            }

            // Cas 2: Génération réussie
            if (responseObj.getString("status").equals("success")) {
                // Vérifier les différents champs possibles dans l'ordre de priorité
                if (responseObj.has("output") && !responseObj.getJSONArray("output").isEmpty()) {
                    return responseObj.getJSONArray("output").getString(0);
                }
                if (responseObj.has("future_links") && !responseObj.getJSONArray("future_links").isEmpty()) {
                    return responseObj.getJSONArray("future_links").getString(0);
                }
                if (responseObj.has("proxy_links") && !responseObj.getJSONArray("proxy_links").isEmpty()) {
                    return responseObj.getJSONArray("proxy_links").getString(0);
                }
                if (responseObj.getJSONObject("meta").has("output")) {
                    JSONArray metaOutput = responseObj.getJSONObject("meta").getJSONArray("output");
                    if (!metaOutput.isEmpty()) {
                        return metaOutput.getString(0);
                    }
                }
            }

            System.err.println("Aucune URL trouvée dans la réponse");
            return null;

        } catch (JSONException | InterruptedException e) {
            System.err.println("Erreur d'analyse: " + e.getMessage());
            return null;
        }
    }

    private String fetchGeneratedImage(String fetchUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fetchUrl))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Réponse fetch: " + response.body()); // Debug supplémentaire
            return parseImageUrl(response.body());

        } catch (Exception e) {
            System.err.println("Échec de la récupération: " + e.getMessage());
            return null;
        }
    }
}