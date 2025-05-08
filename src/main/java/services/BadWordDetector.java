package services;
import java.util.Arrays;
import java.util.List;
public class BadWordDetector {
    private static final List<String> badWords = Arrays.asList(
            "insulte1", "insulte2", "motinterdit"
    );

    // Méthode pour détecter les mots interdits
    public static boolean containsBadWords(String text) {
        String lowerText = text.toLowerCase();
        for (String badWord : badWords) {
            if (lowerText.contains(badWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Option : remplacer les mots interdits par ***
    public static String censorBadWords(String text) {
        String censoredText = text;
        for (String badWord : badWords) {
            censoredText = censoredText.replaceAll("(?i)" + badWord, "***");
        }
        return censoredText;
    }
}

