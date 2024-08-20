package it.chalmers.gamma.app.user.passwordreset;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.security.TimerBlock;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserResetPasswordFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResetPasswordFacade.class);
  private final MailService mailService;
  private final UserRepository userRepository;
  private final PasswordResetRepository passwordResetRepository;
  private final ThrottlingService throttlingService;
  private final String baseUrl;

  public UserResetPasswordFacade(
      AccessGuard accessGuard,
      MailService mailService,
      UserRepository userRepository,
      PasswordResetRepository passwordResetRepository,
      ThrottlingService throttlingService,
      @Value("${application.base-url}") String baseUrl) {
    super(accessGuard);
    this.mailService = mailService;
    this.userRepository = userRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.throttlingService = throttlingService;
    this.baseUrl = baseUrl;
  }

  public String generatePasswordLink(UUID userId) {
    super.accessGuard.require(isAdmin());

    try {
      PasswordResetRepository.PasswordReset passwordReset =
          this.passwordResetRepository.createNewToken(new UserId(userId));

      return generatePasswordResetLink(passwordReset.token());
    } catch (PasswordResetRepository.UserNotFoundException e) {
      throw new IllegalStateException();
    }
  }

  public void startResetPasswordProcess(String cidOrEmailString) {
    this.accessGuard.require(isNotSignedIn());

    TimerBlock.minimum(3000)
        .execute(
            () -> {
              try {
                PasswordResetRepository.PasswordReset passwordReset;
                if (new Email.EmailValidator().validate(cidOrEmailString)
                    instanceof SuccessfulValidation) {
                  passwordReset =
                      this.passwordResetRepository.createNewToken(new Email(cidOrEmailString));
                } else if (new Cid.CidValidator().validate(cidOrEmailString.toLowerCase())
                    instanceof SuccessfulValidation) {
                  passwordReset =
                      this.passwordResetRepository.createNewToken(new Cid(cidOrEmailString.toLowerCase()));
                } else {
                  throw new IllegalArgumentException("Neither an email nor a cid.");
                }

                if (throttlingService.canProceed(
                    passwordReset.email().value() + "-password-reset", 3)) {
                  sendPasswordResetTokenMail(passwordReset.email(), passwordReset.token());
                } else {
                  LOGGER.info("Throttling password reset process triggered.");
                }
              } catch (PasswordResetRepository.UserNotFoundException e) {
                LOGGER.debug(
                    "Someone tried to reset the password for the email {} that doesn't exist",
                    cidOrEmailString);
              }
            });
  }

  public void finishResetPasswordProcess(
      String inputToken, String newPassword, String confirmPassword) {
    this.accessGuard.require(isNotSignedIn());

    if (!newPassword.equals(confirmPassword)) {
      throw new IllegalArgumentException("Please properly confirm password");
    }

    UnencryptedPassword unencryptedPassword = new UnencryptedPassword(newPassword);

    UserId userId = this.passwordResetRepository.useToken(new PasswordResetToken(inputToken));

    this.userRepository.setPassword(userId, unencryptedPassword);
  }

  private String generatePasswordResetLink(PasswordResetToken token) {
    return this.baseUrl + "/forgot-password/finalize?token=" + token.value();
  }

  private void sendPasswordResetTokenMail(Email email, PasswordResetToken token) {
    String subject = "Password reset for Account at IT division of Chalmers";

    String resetUrl = generatePasswordResetLink(token);

    String message =
        """
        A password reset have been requested for this account, if you have not requested this mail, feel free to ignore it.
        The link is valid for 15 minutes. Click here to reset password:
        %s
        """
            .formatted(resetUrl);

    this.mailService.sendMail(email.value(), subject, message);
  }

  public boolean isValidToken(String token) {
    this.accessGuard.require(isNotSignedIn());

    return this.passwordResetRepository.isTokenValid(new PasswordResetToken(token));
  }
}
