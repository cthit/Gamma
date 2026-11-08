package it.chalmers.gamma.app.mail.domain;

public interface MailService {
  void sendMail(String email, String subject, String body);

  /**
   * Sends a security notification to an account's own address. Unlike {@link #sendMail}, it is not
   * limited per client IP, so whoever triggers it can't suppress it by using up that limit first.
   * Callers must limit how often it can be triggered themselves.
   */
  void sendSecurityNotification(String email, String subject, String body);
}
