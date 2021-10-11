package it.chalmers.gamma.app.service;

public interface MailService {

    void sendMail(String email, String subject, String body);

}
