package own.project.privatemessanger.spring;

import org.springframework.stereotype.Component;
import own.project.privatemessanger.app.service.MailProvider;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

@Component
public class MailSender implements MailProvider {

    Logger logger = Logger.getLogger("MailLogger");
    String username = System.getenv("USERNAME");
    String pass = System.getenv("PASS");
    String email = System.getenv("MAIL");
    Path path;
    TextParserProperties textParserProperties;
    TextParser textParser;


    public MailSender(TextParserProperties textParserProperties, TextParser textParser) {
        this.textParserProperties = textParserProperties;
        this.textParser = textParser;
    }

    @Override
    public void sendEmail(String email, String user,String numbers) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pass);
            }
        });
        String s = null;
        try {
            s = createMessage(user,numbers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            logger.info("Preparing Message for " + email);
            Message message = prepareMessage(session, this.email, email,s);
            logger.info("Verification Mail send to " + email);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setPath(Path path){
        this.path = path;
    }

    private Message prepareMessage(Session session, String emailFrom, String emailTo,String messageString) throws MessagingException {
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
        message.setSubject("Verification");

        // Multipart-Message ("Wrapper") erstellen
        Multipart multipart = new MimeMultipart();
        // Body-Part setzen:
        BodyPart messageBodyPart = new MimeBodyPart();
        // Textteil des Body-Parts
        messageBodyPart.setContent(messageString,"text/html; charset=utf-8");
        // Body-Part dem Multipart-Wrapper hinzufügen
        multipart.addBodyPart(messageBodyPart);
        // Message fertigstellen, indem sie mit dem Multipart-Content ausgestattet wird
        message.setContent(multipart,"text/html; charset=utf-8");

        return message;
    }

    private String createMessage(String user , String numbers) throws IOException {
        textParserProperties.insertProperties(List.of(user, numbers));
        return textParser.setPath(Path.of(path.toString())).build();
    }
}
