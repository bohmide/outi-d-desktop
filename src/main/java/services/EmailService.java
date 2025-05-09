package services;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
public class EmailService {
    private static final String SMTP_HOST = "smtp.gmail.com"; // Exemple avec Gmail
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "salmaachour2015@gmail.com";
    private static final String PASSWORD = "pkug ppcd hsfb vjrn";

    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            // Gestion du nom d'expéditeur avec encodage
            message.setFrom(new InternetAddress(USERNAME, "OUTI-D"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Erreur d'encodage du nom d'expéditeur", e);
        }
    }
}