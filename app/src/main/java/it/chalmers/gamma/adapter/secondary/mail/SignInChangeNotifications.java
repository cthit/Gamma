package it.chalmers.gamma.adapter.secondary.mail;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.signin.SignInChanged;
import it.chalmers.gamma.app.user.EmailChangedByAdmin;
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

  @TransactionalEventListener
  public void emailChangedByAdmin(EmailChangedByAdmin change) {
    String previous = change.previousEmail().value();
    try {
      this.users
          .findById(change.userId().value())
          .ifPresent(user -> {
            String message =
                """
                An admin changed the email address of the account %s at the IT division of Chalmers.

                Email recovery is off until the new address is verified through Change email in the profile.

                If you did not ask for this, contact ita@chalmers.it immediately.
                """
                    .formatted(user.cid());
            // The previous address is told as well, since it may be the one the user still reads.
            notifyEmailChange(change, previous, message);
            if (!user.email().equalsIgnoreCase(previous)) {
              notifyEmailChange(change, user.email(), message);
            }
          });
    } catch (RuntimeException failure) {
      LOGGER.error("Could not send email change notification for user {}", change.userId().value());
    }
  }

  private void notifyEmailChange(EmailChangedByAdmin change, String to, String message) {
    try {
      this.mail.sendSecurityNotification(
          to, "The email of your account at the IT division of Chalmers was changed", message);
    } catch (RuntimeException failure) {
      // A mail outage must not roll back, or report failure for, a committed change.
      LOGGER.error("Could not send email change notification for user {}", change.userId().value());
    }
  }
}
