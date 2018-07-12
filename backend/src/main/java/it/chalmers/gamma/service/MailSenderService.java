package it.chalmers.gamma.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.util.Properties;

@Service
@PropertySource("classpath:development.properties")
public class MailSenderService{
    private JavaMailSenderImpl mailSender;

    /*
     * All Mail settings.
     */

    private int port = 587;
    private String host = "smtp.gmail.com";

    @Value("${email.password}")
    private String password;

    @Value("${email.username}")
    private String mail;


    @PostConstruct
    private void setupMailSender(){
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(mail);
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
        System.out.println(mailSender.getUsername());
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        email.setFrom("no-reply.GammaLogin@chalmers.it");
        mailSender.send(email);
    }
}
