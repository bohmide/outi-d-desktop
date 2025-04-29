package utils;

import com.google.gson.Gson;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class TelegraphUploader {

    private static final String API_URL = "https://api.telegra.ph/createPage";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public static String createTelegraphPage(String title, String contentHtml) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("access_token", "f1c5235ae2277f918af2e4eef8673581f58a51d117438c979a83f5b57c0d") // Not needed for anonymous posts
                .add("title", title)
                .add("author_name", "Professeur")
                .add("content", "[{\"tag\":\"p\",\"children\":[\"" + contentHtml + "\"]}]")
                .add("return_content", "false")
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            TelegraphResponse result = gson.fromJson(responseBody, TelegraphResponse.class);
            return result.result.url;
        }
    }


    public static String uploadImageToTelegraph(File imageFile) throws IOException {
        String boundary = "----WebKitFormBoundary" + Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        HttpURLConnection connection = (HttpURLConnection) new URL("https://telegra.ph/upload").openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (OutputStream output = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)) {

            // Start of multipart/form-data
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(imageFile.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(getContentType(imageFile)).append(CRLF);
            writer.append(CRLF).flush();

            // File content
            Files.copy(imageFile.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();

            // End of multipart/form-data
            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        // Read response
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println(errorLine);
                }
            }
            throw new IOException("Failed to upload image: HTTP " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder responseText = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseText.append(line);
            }

            // Parse JSON response
            JSONArray jsonResponse = new JSONArray(responseText.toString());
            if (jsonResponse.length() == 0) {
                throw new IOException("Empty response from server");
            }

            JSONObject obj = jsonResponse.getJSONObject(0);
            if (obj.has("src")) {
                return "https://telegra.ph" + obj.getString("src");
            } else {
                throw new IOException("Upload failed: " + obj.toString());
            }
        }
    }

    private static String getContentType(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            // Fallback for common image types if probe fails
            String name = file.getName().toLowerCase();
            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (name.endsWith(".png")) {
                return "image/png";
            } else if (name.endsWith(".gif")) {
                return "image/gif";
            }
            return "application/octet-stream";
        }
        return contentType;
    }


    static class TelegraphResponse {
        boolean ok;
        Result result;

        static class Result {
            String url;
        }
    }



}
