package org.example.utils.user;

import java.io.*;
import java.util.concurrent.*;

public class FaceAuthenticator {

    public CompletableFuture<Boolean> authenticateAsync(File imageFile, String pythonScriptPath, String dbPath) {
        return CompletableFuture.supplyAsync(() -> {
            ProcessBuilder pb = new ProcessBuilder(
                    "py",
                    pythonScriptPath,
                    imageFile.getAbsolutePath(),
                    dbPath
            );

            pb.redirectErrorStream(true);

            try {
                Process process = pb.start();

                // Lire la sortie du processus
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        System.out.println("Python output: " + line);
                    }
                }

                // Attendre la fin du processus avec timeout
                if (!process.waitFor(30, TimeUnit.SECONDS)) {
                    process.destroy();
                    throw new RuntimeException("Python script execution timed out");
                }

                // Vérifier le code de sortie
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    System.err.println("Python script failed with exit code: " + exitCode);
                    System.err.println("Output: " + output);
                    return false;
                }

                // Vérifier la sortie pour "True"
                return output.toString().trim().contains("True");

            } catch (Exception e) {
                System.err.println("Error during authentication: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        });
    }
}
