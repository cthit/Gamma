package it.chalmers.gamma.adapter.secondary.mail;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.signin.SignInChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SignInChangeNotifications {
  private static final Logger LOGGER = LoggerFactory.getLogger(SignInChangeNotifications.class);
  private final UserJpaRepository users;
  private final MailService mail;

  public SignInChangeNotifications(UserJpaRepository users, MailService mail) {
    this.users = users;
    this.mail = mail;
  }

  @TransactionalEventListener
  public void changed(SignInChanged change) {
    try {
      this.users
          .findById(change.userId().value())
          .ifPresent(user ->
              this.mail.sendSecurityNotification(
                  user.email(),
                  "Your sign-in security at the IT division of Chalmers changed",
                  "The sign-in security of account "
                      + user.cid()
                      + " changed.\n\n"
                      + change.description()
                      + "\n\nIf you did not do this, contact ita@chalmers.it immediately."));
    } catch (RuntimeException failure) {
      // A mail outage must not roll back, or report failure for, a committed security change.
      LOGGER.error(
          "Could not send sign-in security notification for user {}", change.userId().value());
    }
  }
}
