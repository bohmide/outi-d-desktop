package services;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class EmailService {
    private final String username = "mouhamedaminebenali8@gmail.com";
    private final String password = "lcsuuzpadlpxmnsx";

    public void sendEmailWithImage(String to, String subject, String htmlContent, String imageUrl) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            // Création du contenu multipart
            MimeMultipart multipart = new MimeMultipart("related");

            // Partie HTML
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            // Partie image
            if(imageUrl != null && !imageUrl.isEmpty()) {
                MimeBodyPart imagePart = new MimeBodyPart();

                // Télécharger l'image depuis l'URL
                try (InputStream imageStream = new URL(imageUrl).openStream()) {
                    imagePart.setDataHandler(new DataHandler(new ByteArrayDataSource(imageStream, "image/jpeg")));
                    imagePart.setHeader("Content-ID", "<certificate-image>");
                    imagePart.setDisposition(MimeBodyPart.INLINE);
                    multipart.addBodyPart(imagePart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erreur d'envoi d'email: " + e.getMessage());
        }
    }
}