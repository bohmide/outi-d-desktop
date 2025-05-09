package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
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
            {"role": "user", "content": "%s"}
          ]
        }
        """.formatted(question);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec l'IA.";
        }
    }

}
