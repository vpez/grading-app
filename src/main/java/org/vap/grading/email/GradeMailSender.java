package org.vap.grading.email;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author Vahe Pezeshkian
 *         September 19, 2017
 */
public class GradeMailSender {

    private Authenticator authenticator;
    private JavaMailSenderImpl mailSender;
    private String subject;

    public GradeMailSender(Authenticator authenticator) {
        this.authenticator = authenticator;
        this.mailSender = new JavaMailSenderImpl();
        this.mailSender.setSession(initSession());
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void send(String addressTo, String content) throws MessagingException {
        send(addressTo, null, subject, content);
    }

    public void send(String addressTo, String subject, String content) throws MessagingException {
        send(addressTo, null, subject, content);
    }

    public void send(String addressTo, String addressCC, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(addressTo);

        if (addressCC != null) {
            helper.setCc(addressCC);
        }

        if (subject != null) {
            helper.setSubject(subject);
        }

        helper.setText(content);

        System.out.println("Sending mail to " + addressTo);
        mailSender.send(message);
    }

    private Session initSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return Session.getInstance(props, authenticator);
    }
}
