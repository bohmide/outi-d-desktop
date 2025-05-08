package services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject; // Doit être présent
import java.awt.Color;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.InputStream; // Ajouté en haut du fichier

public class PDFService {

    public static class PdfStyleConfig {
        public Color startGradient = new Color(33, 150, 243); // Début dégradé
        public Color endGradient = new Color(173, 216, 230); // Fin dégradé
        public Color labelColor = new Color(102, 102, 102);
        public Color valueColor = new Color(51, 51, 51);
        public float margin = 40;
        public float logoWidth = 110;
        public PDType1Font titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        public PDType1Font contentFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        public int titleSize = 22;
        public int contentSize = 12;
    }


    private PdfStyleConfig styleConfig = new PdfStyleConfig();
    private String logoPath;
    public void generateCompetitionPDF(String title, String organisation, String dates,
                                       String description, String localisation,
                                       String filePath) throws IOException {
        generateCompetitionPDF(title, organisation, dates, description, localisation,
                filePath, "/images/LOGO.png", styleConfig); // Chemin relatif aux ressources
    }

    public void generateCompetitionPDF(String title, String organisation, String dates,
                                       String description, String localisation,
                                       String filePath, String logoPath,
                                       PdfStyleConfig customStyle) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PdfStyleConfig style = (customStyle != null) ? customStyle : this.styleConfig;

                // Dessiner le fond dégradé
                drawGradientBackground(contentStream, page, style);

                // Charger le logo depuis les ressources
                PDImageXObject logo = null;
                try (InputStream is = getClass().getResourceAsStream(logoPath)) {
                    if (is != null) {
                        logo = PDImageXObject.createFromByteArray(document,
                                is.readAllBytes(), "logo");
                    } else {
                        System.err.println("Logo introuvable dans les ressources: " + logoPath);
                    }
                } catch (IOException e) {
                    System.err.println("Erreur de lecture du logo: " + e.getMessage());
                }

                float pageWidth = page.getMediaBox().getWidth();
                float yPosition = page.getMediaBox().getHeight() - style.margin;

                // Dessiner l'en-tête avec logo
                drawHeader(contentStream, pageWidth, yPosition, title, logo, style);
                yPosition -= 120;

                // Contenu principal
                contentStream.setLeading(18f);
                yPosition = addFormattedField(contentStream, "Organisation", organisation, style.margin, yPosition, style);
                yPosition = addFormattedField(contentStream, "Dates", dates, style.margin, yPosition, style);
                yPosition = addFormattedField(contentStream, "Localisation", localisation, style.margin, yPosition, style);
                yPosition -= 20;

                // Section Description
                yPosition = addDescription(contentStream, description, style.margin, yPosition, style);
            }

            document.save(filePath);
        }
    }

    private void drawGradientBackground(PDPageContentStream contentStream, PDPage page, PdfStyleConfig style)
            throws IOException {
        int steps = 50;
        float pageHeight = page.getMediaBox().getHeight();
        float pageWidth = page.getMediaBox().getWidth();
        float stepHeight = pageHeight / steps;

        for (int i = 0; i < steps; i++) {
            float ratio = (float) i / steps;
            Color color = interpolateColor(style.startGradient, style.endGradient, ratio);

            contentStream.setNonStrokingColor(color);
            contentStream.addRect(0, pageHeight - (i * stepHeight), pageWidth, stepHeight);
            contentStream.fill();
        }
    }

    private Color interpolateColor(Color start, Color end, float ratio) {
        int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
        int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
        int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
        return new Color(red, green, blue);
    }

    private void drawHeader(PDPageContentStream contentStream, float pageWidth, float yStart,
                            String title, PDImageXObject logo, PdfStyleConfig style) throws IOException {
        // Dessiner le logo avec proportions correctes
        if (logo != null) {
            float aspectRatio = (float) logo.getHeight() / logo.getWidth();
            float logoHeight = style.logoWidth * aspectRatio;
            float logoX = pageWidth - style.logoWidth - style.margin;
            float logoY = yStart - 50; // Ajustement position verticale
            System.out.println("Logo chargé - Dimensions: "
                    + logo.getWidth() + "x" + logo.getHeight());
            contentStream.drawImage(logo, logoX, logoY, style.logoWidth, logoHeight);
        }else {
            System.err.println("Aucun logo disponible");
        }

        // Dessiner le titre
        contentStream.beginText();
        contentStream.setNonStrokingColor(new Color(255, 255, 255, 200)); // Blanc semi-transparent
        contentStream.setFont(style.titleFont, style.titleSize);
        contentStream.newLineAtOffset(style.margin, yStart - 60);
        contentStream.showText(title);
        contentStream.endText();
    }


    private float addFormattedField(PDPageContentStream contentStream, String label,
                                    String value, float x, float y, PdfStyleConfig style) throws IOException {
        // Label
        contentStream.beginText();
        contentStream.setNonStrokingColor(style.labelColor);
        contentStream.setFont(style.contentFont, style.contentSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(label + ":");
        contentStream.endText();

        // Valeur
        contentStream.beginText();
        contentStream.setNonStrokingColor(style.valueColor);
        contentStream.setFont(style.contentFont, style.contentSize);
        contentStream.newLineAtOffset(x + 80, y);
        contentStream.showText(value);
        contentStream.endText();

        return y - 25;
    }

    private float addDescription(PDPageContentStream contentStream, String description,
                                 float x, float y, PdfStyleConfig style) throws IOException {
        // Titre section
        contentStream.beginText();
        contentStream.setNonStrokingColor(style.labelColor);
        contentStream.setFont(style.contentFont, style.contentSize + 2);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText("Description");
        contentStream.endText();

        y -= 20;

        // Contenu
        String[] lines = description.split("\n");
        contentStream.beginText();
        contentStream.setNonStrokingColor(style.valueColor);
        contentStream.setFont(style.contentFont, style.contentSize);
        contentStream.setLeading(16f);
        contentStream.newLineAtOffset(x, y);

        for (String line : lines) {
            contentStream.showText(line);
            contentStream.newLine();
        }
        contentStream.endText();

        return y - (lines.length * 16);
    }

    // Setters pour la personnalisation
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void configureStyle(PdfStyleConfig styleConfig) {
        this.styleConfig = styleConfig;
    }
}