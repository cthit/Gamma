package it.chalmers.gamma.app.user.passwordreset;

import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserResetPasswordFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResetPasswordFacade.class);
  private final MailService mailService;
  private final UserRepository userRepository;
  private final PasswordResetRepository passwordResetRepository;
  private final ThrottlingService throttlingService;

  public UserResetPasswordFacade(
      AccessGuard accessGuard,
      MailService mailService,
      UserRepository userRepository,
      PasswordResetRepository passwordResetRepository,
      ThrottlingService throttlingService) {
    super(accessGuard);
    this.mailService = mailService;
    this.userRepository = userRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.throttlingService = throttlingService;
  }

  public void startResetPasswordProcess(String cidOrEmailString)
      throws PasswordResetProcessException {
    this.accessGuard.require(isNotSignedIn());

    try {
      PasswordResetRepository.PasswordReset passwordReset;
      if (new Email.EmailValidator().validate(cidOrEmailString) instanceof SuccessfulValidation) {
        passwordReset = this.passwordResetRepository.createNewToken(new Email(cidOrEmailString));
      } else if (new Cid.CidValidator().validate(cidOrEmailString)
          instanceof SuccessfulValidation) {
        passwordReset = this.passwordResetRepository.createNewToken(new Cid(cidOrEmailString));
      } else {
        throw new IllegalArgumentException("Neither an email nor a cid.");
      }

      if (throttlingService.canProceed(passwordReset.email().value() + "-password-reset", 3)) {
        sendPasswordResetTokenMail(passwordReset.email(), passwordReset.token());
      } else {
        LOGGER.info("Throttling password reset process triggered.");
      }
    } catch (PasswordResetRepository.UserNotFoundException e) {
      LOGGER.debug(
          "Someone tried to reset the password for the email {} that doesn't exist",
          cidOrEmailString);
      throw new PasswordResetProcessException();
    }
  }

  public void finishResetPasswordProcess(
      String emailString, String inputTokenRaw, String newPassword, String confirmPassword)
      throws PasswordResetProcessException {
    this.accessGuard.require(isNotSignedIn());

    if (!newPassword.equals(confirmPassword)) {
      throw new IllegalArgumentException("please properly confirm password");
    }

    Email email = new Email(emailString);

    Optional<GammaUser> maybeUser = this.userRepository.get(email);

    if (maybeUser.isEmpty()) {
      LOGGER.debug(
          "Someone tried to finish the reset value process for "
              + emailString
              + " that doesn't exist");
      throw new PasswordResetProcessException();
    }

    GammaUser user = maybeUser.get();
    Optional<PasswordResetToken> maybeToken = this.passwordResetRepository.getToken(user.id());

    if (maybeToken.isEmpty()) {
      LOGGER.debug("No code exists for the user " + user);
      throw new PasswordResetProcessException();
    }

    PasswordResetToken token = maybeToken.get();
    PasswordResetToken inputToken = new PasswordResetToken(inputTokenRaw);

    if (token.equals(inputToken)) {
      this.passwordResetRepository.removeToken(token);
      this.userRepository.setPassword(user.id(), new UnencryptedPassword(newPassword));
    } else {
      LOGGER.debug("Incorrect value reset code for user " + user);
      throw new PasswordResetProcessException();
    }
  }

  private void sendPasswordResetTokenMail(Email email, PasswordResetToken token) {
    String subject = "Password reset for Account at IT division of Chalmers";
    String message =
        "A password reset have been requested for this account, if you have not requested "
            + "this mail, feel free to ignore it. \n Your reset code : "
            + token.value();

    this.mailService.sendMail(email.value(), subject, message);
  }

  // Vague for security reasons
  public static class PasswordResetProcessException extends Exception {}
}
