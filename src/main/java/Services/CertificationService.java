package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.properties.*;
import com.itextpdf.kernel.colors.ColorConstants;
import org.apache.commons.codec.binary.Base64;


import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.properties.TextAlignment;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class CertificationService {
    private static final String CERTIFICATION_DIRECTORY = "D:/3emeA42/JavaSem1/outi-d-desktop/src/main/resources/certifications";
    private static final String APPLICATION_NAME = "Certification Generator";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String LOGO_PATH = "D:/3emeA42/JavaSem1/outi-d-desktop/src/main\\resources\\LOGO.png\"";

    /**
     * Méthode principale pour générer et envoyer la certification
     */
    public static void genererEtEnvoyerCertification(String studentName, String studentEmail, String courseName, int idCours, int score, int totalQuestions)
            throws IOException, GeneralSecurityException, MessagingException {
        String filePath = genererCertification(studentName, courseName, idCours, score, totalQuestions);
        envoyerCertificationParGmail(studentEmail, filePath, courseName);
    }

    /**
     * Génère le PDF de certification
     */

    public static String genererCertification(String studentName, String courseName, int idCours, int score, int totalQuestions) throws IOException {
        File directory = new File(CERTIFICATION_DIRECTORY);
        if (!directory.exists()) directory.mkdirs();

        String filename = CERTIFICATION_DIRECTORY + "/Certification_" + idCours + "_" + studentName.replaceAll("\\s+", "_") + ".pdf";

        try (PdfWriter writer = new PdfWriter(filename);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // ===== 1. EN-TÊTE PROFESSIONNEL ===== //
            // Tableau à 2 colonnes pour logo et QR code
            float[] columnWidths = {45f, 55f};
            Table headerTable = new Table(columnWidths)
                    .setWidth(UnitValue.createPercentValue(90))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(20);

            // Logo (left)
            Image logo = new Image(ImageDataFactory.create("D:/3emeA42/JavaSem1/outi-d-desktop/src/main/resources/LOGO.png"))
                    .setAutoScale(true)
                    .setMaxWidth(100);
            headerTable.addCell(new Cell()
                    .add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // QR Code (right)
            String qrData = String.format(
                    "CERTIF Outi-D\nÉtudiant: %s\nCours: %s\nScore: %d/%d\nID: %d",
                    studentName, courseName, score, totalQuestions, idCours);

            Image qrImage = new Image(new BarcodeQRCode(qrData).createFormXObject(pdf))
                    .setWidth(80)
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            headerTable.addCell(new Cell()
                    .add(qrImage)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.add(headerTable);

            // ===== 2. CORPS DU DOCUMENT ===== //
            // Titre principal
            document.add(new Paragraph("CERTIFICATION DE RÉUSSITE")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setMarginBottom(20));

            // Section étudiant
            Div studentDiv = new Div()
                    .setBackgroundColor(new DeviceRgb(240, 240, 240))
                    .setPadding(15)
                    .setMarginBottom(20);

            studentDiv.add(new Paragraph("FÉLICITATIONS")
                    .setFontSize(16)
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER));

            studentDiv.add(new Paragraph(studentName.toUpperCase())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(5));

            document.add(studentDiv);

            // Détails du cours
            float[] courseWidths = {40f, 60f};
            Table courseTable = new Table(courseWidths)
                    .setWidth(UnitValue.createPercentValue(80))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(15);

            courseTable.addCell(createStyledCell("Cours:", true));
            courseTable.addCell(createStyledCell(courseName, false));

            courseTable.addCell(createStyledCell("Score:", true));
            courseTable.addCell(createStyledCell(score + "/" + totalQuestions, false));

            courseTable.addCell(createStyledCell("Date:", true));
            courseTable.addCell(createStyledCell(java.time.LocalDate.now().toString(), false));

            document.add(courseTable);

            // Pied de page
            document.add(new Paragraph("Outi-D® - Certification valide sans signature")
                    .setFontSize(10)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));

        }
        return filename;
    }

    private static Cell createStyledCell(String text, boolean isHeader) {
        Paragraph p = new Paragraph(text)
                .setFontSize(isHeader ? 12 : 11)
                .setFontColor(isHeader ? ColorConstants.DARK_GRAY : ColorConstants.BLACK);

        if (isHeader) {
            p.setBold();
        }

        return new Cell()
                .add(p)
                .setPadding(5)
                .setBorder(Border.NO_BORDER);
    }
    /**
     * Envoie la certification via Gmail API
     */
    private static void envoyerCertificationParGmail(String recipientEmail, String filePath, String courseName)
            throws IOException, GeneralSecurityException, MessagingException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        MimeMessage email = createEmailWithAttachment(
                "me", // L'adresse 'me' représente l'utilisateur authentifié
                recipientEmail,
                "Certification pour le cours " + courseName,
                "Félicitations! Veuillez trouver ci-joint votre certification.",
                new File(filePath)
        );

        Message message = createGmailMessage(email);
        service.users().messages().send("me", message).execute();
    }

    /**
     * Convertit un MimeMessage en Message Gmail
     */
    private static Message createGmailMessage(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Crée un email avec pièce jointe
     */
    private static MimeMessage createEmailWithAttachment(String from, String to, String subject, String bodyText, File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);

        // Corps du message
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(bodyText);

        // Pièce jointe
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setDataHandler(new DataHandler(new FileDataSource(file)));
        attachmentPart.setFileName(file.getName());

        // Assemblage
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        email.setContent(multipart);
        return email;
    }

    /**
     * Gère l'authentification OAuth2
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = CertificationService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) throw new FileNotFoundException("Fichier credentials.json introuvable");

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}