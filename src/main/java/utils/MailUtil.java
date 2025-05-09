package utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;


import java.util.Properties;

public class MailUtil {
    private static final String USERNAME = "f151b87b4d64de";
    private static final String PASSWORD = "a44454ad764304";
    private static final String HOST = "sandbox.smtp.mailtrap.io";
    private static final String PORT = "587";

    public static void reserveEvent(String fromEmail, String toEmail, String eventName, String eventDate/*, String eventLocation*/) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("🎉 Reservation Confirmed: " + eventName);

            // Create beautiful HTML content
            String htmlContent = "<!DOCTYPE html>" +
                    "<html lang='fr'>" +
                    "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<style>" +
                    "body { font-family: Arial, sans-serif; background: #f6f6f6; padding: 20px; }" +
                    ".container { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }" +
                    "h1 { color: #4CAF50; }" +
                    "p { font-size: 16px; color: #555; }" +
                    ".footer { margin-top: 20px; font-size: 12px; color: #aaa; }" +
                    "button { background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin-top: 20px; }" +
                    "button:hover { background: #45a049; }" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h1>Réservation Confirmée !</h1>" +
                    "<p>Bonjour,</p>" +
                    "<p>Merci d'avoir réservé votre place pour <strong>" + eventName + "</strong> !</p>" +
                    "<p><strong>Date :</strong> " + eventDate + "<br>" +
                    "<p>Nous avons hâte de vous accueillir !</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";


            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(htmlContent, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Reservation email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
