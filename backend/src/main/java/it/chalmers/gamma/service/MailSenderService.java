package it.chalmers.gamma.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Properties;

@Service
public class MailSenderService{
    JavaMailSenderImpl mailSender;

    /*
     * All Mail settings.
     */
    int port = 587;
    String host = "smtp.gmail.com";
    String username = "sampleMail@chalmers.it";
    String password = "defaultPassword";   //Change this to ENV variables.


    public MailSenderService(){
        setupMailSender();
    }
    private void setupMailSender(){
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        mailSender.setJavaMailProperties(props);
    }
    public void sendMessage(String to, String subject, String message) throws MessagingException {
        mailSender.testConnection();
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("no-reply.GammaLogin@chalmers.it");
        mailSender.send(email);
    }
}
