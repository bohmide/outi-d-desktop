package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatBotController {
    @FXML
    private TextArea userQuestion;
    @FXML
    private TextArea chatResponse;
    @FXML
    private void handleEnvoyerQuestion() {
        String question = userQuestion.getText();
        if (question.isEmpty()) return;

        String reponseIA = appelerChatbot(question);
        chatResponse.setText(reponseIA);
    }
    private String appelerChatbot(String question) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String apiKey = "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";

            String requestBody = """
            {
              "model": "mistralai/mistral-7b-instruct",
              "messages": [
                {"role": "system", "content": "Tu es un assistant pédagogique pour étudiants."},
                {"role": "user",   "content": "%s"}
              ]
            }
            """.formatted(question);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Body:   " + response.body());

            if (response.statusCode() != 200) {
                return "Erreur HTTP " + response.statusCode();
            }

            JSONObject root = new JSONObject(response.body());
            if (root.has("error")) {
                return "API error: " +
                        root.getJSONObject("error").optString("message");
            }

            JSONArray choices = root.optJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                return "Aucune réponse de l’IA.";
            }

            return ((JSONArray) choices)
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec l'IA.";
        }
    }


}
