package it.chalmers.gamma.app.mail;

public interface MailService {

    void sendMail(String email, String subject, String body);

}
