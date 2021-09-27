package it.chalmers.gamma.app.port.service;

public interface MailService {

    void sendMail(String email, String subject, String body);

}
