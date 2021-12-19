package it.chalmers.gamma.app.mail.domain;

public interface MailService {

    void sendMail(String email, String subject, String body);

}
