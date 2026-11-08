package it.chalmers.gamma.app.user.emailchange;

import static it.chalmers.gamma.app.authentication.AccessGuard.isSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeRepository.EmailChange;
import it.chalmers.gamma.app.user.emailchange.domain.EmailChangeToken;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Lets a signed in user change their email. The user must give their current password, and the
 * email is only changed once they open the confirmation link that is mailed to the new address.
 */
@Service
public class UserEmailChangeFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEmailChangeFacade.class);
  private static final int MAX_ATTEMPTS_PER_DAY = 10;
  private static final int MAX_REQUESTS_PER_DAY = 5;

  private final UserRepository userRepository;
  private final EmailChangeRepository emailChangeRepository;
  private final MailService mailService;
  private final ThrottlingService throttlingService;
  private final String baseUrl;

  public UserEmailChangeFacade(
      AccessGuard accessGuard,
      UserRepository userRepository,
      EmailChangeRepository emailChangeRepository,
      MailService mailService,
      ThrottlingService throttlingService,
      @Value("${application.base-url}") String baseUrl) {
    super(accessGuard);
    this.userRepository = userRepository;
    this.emailChangeRepository = emailChangeRepository;
    this.mailService = mailService;
    this.throttlingService = throttlingService;
    this.baseUrl = baseUrl;
  }

  /** Mails a confirmation link to the new email. The email is not changed yet. */
  public void requestEmailChange(String newEmailString, String currentPassword)
      throws PasswordIncorrectException,
          EmailUnchangedException,
          EmailAlreadyInUseException,
          TooManyRequestsException {
    GammaUser me = signedInUser();

    // Counts every attempt, so a stolen session can't be used to guess the password or to probe
    // which emails are registered.
    if (!this.throttlingService.canProceed(
        me.id().value() + "-email-change-attempt", MAX_ATTEMPTS_PER_DAY)) {
      LOGGER.info("Throttling email change attempts for user {}", me.id().value());
      throw new TooManyRequestsException();
    }

    if (!isCurrentPassword(me, currentPassword)) {
      throw new PasswordIncorrectException();
    }

    Email newEmail = new Email(newEmailString);

    if (newEmail.value().equalsIgnoreCase(me.extended().email().value())) {
      throw new EmailUnchangedException();
    }

    if (this.userRepository.get(newEmail).isPresent()) {
      throw new EmailAlreadyInUseException();
    }

    if (!this.throttlingService.canProceed(
        me.id().value() + "-email-change", MAX_REQUESTS_PER_DAY)) {
      LOGGER.info("Throttling email change requests for user {}", me.id().value());
      throw new TooManyRequestsException();
    }

    EmailChangeToken token = this.emailChangeRepository.create(me.id(), newEmail);

    sendConfirmationMail(newEmail, token);
  }

  /** The email a valid link would change to, if the link belongs to the signed in user. */
  public Optional<String> getPendingEmail(String token) {
    GammaUser me = signedInUser();

    return this.emailChangeRepository
        .get(new EmailChangeToken(token), me.id())
        .map(change -> change.newEmail().value());
  }

  /** Changes the email of the signed in user and returns the new email. */
  public String confirmEmailChange(String token)
      throws InvalidTokenException, EmailAlreadyInUseException {
    GammaUser me = signedInUser();
    Email oldEmail = me.extended().email();

    EmailChange change =
        this.emailChangeRepository
            .use(new EmailChangeToken(token), me.id())
            .orElseThrow(InvalidTokenException::new);

    // Another account may have taken the email since the link was sent. The database only
    // enforces exact uniqueness, while emails are looked up ignoring case.
    if (this.userRepository
        .get(change.newEmail())
        .filter(user -> !user.id().equals(me.id()))
        .isPresent()) {
      throw new EmailAlreadyInUseException();
    }

    try {
      this.userRepository.setEmail(me.id(), change.newEmail());
    } catch (UserRepository.EmailAlreadyInUseException e) {
      throw new EmailAlreadyInUseException();
    }

    LOGGER.info("User {} confirmed a change of their email", me.id().value());

    sendChangedNotificationMail(me, oldEmail, change.newEmail());

    return change.newEmail().value();
  }

  /**
   * A user without a password confirms that it is them by having recently signed in with a passkey
   * instead.
   */
  private boolean isCurrentPassword(GammaUser me, String candidate) {
    if (!this.userRepository.hasPassword(me.id())) {
      return AuthenticationExtractor.recentlySignedInWithPasskey();
    }

    UnencryptedPassword password;
    try {
      password = new UnencryptedPassword(candidate);
    } catch (IllegalArgumentException e) {
      // Too short or empty to be a valid password, so it can't be the current one.
      return false;
    }

    return this.userRepository.checkPassword(me.id(), password);
  }

  private GammaUser signedInUser() {
    this.accessGuard.require(isSignedIn());

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthentication) {
      return userAuthentication.gammaUser();
    }

    throw new IllegalCallerException("Can only be called by signed in users");
  }

  private void sendConfirmationMail(Email newEmail, EmailChangeToken token) {
    String link = this.baseUrl + "/me/confirm-email?token=" + token.value();

    String message =
        """
        Someone asked to use this email for an account at the IT division of Chalmers.
        If that was you, open the link below while signed in to confirm it. The link is valid for %d minutes.
        %s

        If you didn't ask for this, you can ignore this mail and nothing will change.
        """
            .formatted(EmailChangeRepository.VALIDITY.toMinutes(), link);

    this.mailService.sendMail(
        newEmail.value(), "Confirm your new email at the IT division of Chalmers", message);
  }

  private void sendChangedNotificationMail(GammaUser me, Email oldEmail, Email newEmail) {
    String message =
        """
        The email for the account %s at the IT division of Chalmers was changed from this address to %s.
        If you didn't do this, contact ita@chalmers.it as soon as possible.
        """
            .formatted(me.cid().value(), newEmail.value());

    this.mailService.sendSecurityNotification(
        oldEmail.value(), "Your email at the IT division of Chalmers was changed", message);
  }

  public static final class PasswordIncorrectException extends Exception {}

  public static final class EmailUnchangedException extends Exception {}

  public static final class EmailAlreadyInUseException extends Exception {}

  public static final class TooManyRequestsException extends Exception {}

  public static final class InvalidTokenException extends Exception {}
}
