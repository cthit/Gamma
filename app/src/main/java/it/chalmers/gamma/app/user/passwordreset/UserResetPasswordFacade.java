package it.chalmers.gamma.app.user.passwordreset;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.signin.SignInChanged;
import it.chalmers.gamma.app.signin.domain.PasskeyRepository;
import it.chalmers.gamma.app.signin.domain.TotpRepository;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import it.chalmers.gamma.app.validation.SuccessfulValidation;
import it.chalmers.gamma.security.TimerBlock;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class UserResetPasswordFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserResetPasswordFacade.class);
  private final MailService mailService;
  private final UserRepository userRepository;
  private final PasswordResetRepository passwordResetRepository;
  private final ThrottlingService throttlingService;
  private final AdminRepository adminRepository;
  private final TotpRepository totpRepository;
  private final PasskeyRepository passkeyRepository;
  private final ApplicationEventPublisher events;
  private final String baseUrl;

  public UserResetPasswordFacade(
      AccessGuard accessGuard,
      MailService mailService,
      UserRepository userRepository,
      PasswordResetRepository passwordResetRepository,
      ThrottlingService throttlingService,
      AdminRepository adminRepository,
      TotpRepository totpRepository,
      PasskeyRepository passkeyRepository,
      ApplicationEventPublisher events,
      @Value("${application.base-url}") String baseUrl) {
    super(accessGuard);
    this.adminRepository = adminRepository;
    this.totpRepository = totpRepository;
    this.passkeyRepository = passkeyRepository;
    this.events = events;
    this.mailService = mailService;
    this.userRepository = userRepository;
    this.passwordResetRepository = passwordResetRepository;
    this.throttlingService = throttlingService;
    this.baseUrl = baseUrl;
  }

  public String generatePasswordLink(UUID userId) throws EmailRecoveryNotAllowedException {
    super.accessGuard.require(isAdmin());

    if (!canRecoverByEmail(new UserId(userId))) {
      throw new EmailRecoveryNotAllowedException();
    }

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
        .execute(() -> {
          try {
            Optional<UserId> userId = findUser(cidOrEmailString);
            if (userId.isPresent() && !canRecoverByEmail(userId.get())) {
              LOGGER.info(
                  "Not mailing a password reset link to {}, who can't recover by email",
                  userId.get().value());
              return;
            }

            PasswordResetRepository.PasswordReset passwordReset;
            if (new Email.EmailValidator().validate(cidOrEmailString)
                instanceof SuccessfulValidation) {
              passwordReset =
                  this.passwordResetRepository.createNewToken(new Email(cidOrEmailString));
            } else if (new Cid.CidValidator().validate(cidOrEmailString.toLowerCase())
                instanceof SuccessfulValidation) {
              passwordReset =
                  this.passwordResetRepository.createNewToken(
                      new Cid(cidOrEmailString.toLowerCase()));
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

  @Transactional
  public void finishResetPasswordProcess(
      String inputToken, String newPassword, String confirmPassword) {
    this.accessGuard.require(isNotSignedIn());

    if (!newPassword.equals(confirmPassword)) {
      throw new IllegalArgumentException("Please properly confirm password");
    }

    UnencryptedPassword unencryptedPassword = new UnencryptedPassword(newPassword);

    PasswordResetToken token = new PasswordResetToken(inputToken);
    UserId userId = lockRecoverableOwner(token);
    this.passwordResetRepository.useToken(token);

    this.userRepository.setPassword(userId, unencryptedPassword);
    this.passkeyRepository.deleteAll(userId);
    this.userRepository.revokeSessions(userId);
    this.events.publishEvent(
        new SignInChanged(
            userId,
            "Your password was reset through email. Old passkeys and sessions were revoked. An existing authenticator app is still required."));
  }

  public Optional<ResetOptions> getResetOptions(String token) {
    this.accessGuard.require(isNotSignedIn());

    return this.passwordResetRepository
        .getUserOfValidToken(new PasswordResetToken(token))
        .filter(this::canRecoverByEmail)
        .map(userId -> new ResetOptions(!this.totpRepository.isEnabled(userId)));
  }

  @Transactional
  public PasskeySetup useTokenToSetUpPasskey(String inputToken) {
    this.accessGuard.require(isNotSignedIn());

    PasswordResetToken token = new PasswordResetToken(inputToken);
    UserId owner = lockRecoverableOwner(token);
    if (this.totpRepository.isEnabled(owner)) {
      throw new IllegalArgumentException("Can only reset the password");
    }

    this.passwordResetRepository.useToken(token);
    this.passkeyRepository.deleteAll(owner);
    this.userRepository.removePassword(owner);
    this.userRepository.revokeSessions(owner);
    this.events.publishEvent(
        new SignInChanged(
            owner,
            "Email recovery was used to set up a new sign-in method. Your old credentials and sessions were revoked."));
    Instant startedAt = this.userRepository.sessionsRevokedAt(owner).orElseThrow().plusMillis(1);
    return new PasskeySetup(owner.value(), startedAt);
  }

  private boolean canRecoverByEmail(UserId userId) {
    try {
      return !this.adminRepository.isAdmin(userId)
          && this.userRepository.isEmailRecoveryEnabled(userId);
    } catch (UserRepository.UserNotFoundException e) {
      return false;
    }
  }

  private UserId lockRecoverableOwner(PasswordResetToken token) {
    UserId owner =
        this.passwordResetRepository
            .getUserOfValidToken(token)
            .orElseThrow(PasswordResetRepository.TokenNotFoundRuntimeException::new);
    this.userRepository.lockSignInMethods(owner);
    if (!canRecoverByEmail(owner)) {
      throw new PasswordResetRepository.TokenNotFoundRuntimeException();
    }
    return owner;
  }

  private Optional<UserId> findUser(String cidOrEmail) {
    String identifier = cidOrEmail.toLowerCase();
    if (new Email.EmailValidator().validate(identifier) instanceof SuccessfulValidation) {
      return this.userRepository.findId(new Email(identifier));
    }
    if (new Cid.CidValidator().validate(identifier) instanceof SuccessfulValidation) {
      return this.userRepository.findId(new Cid(identifier));
    }
    return Optional.empty();
  }

  public record ResetOptions(boolean canSetUpPasskey) {}

  public record PasskeySetup(UUID userId, Instant startedAt) {}

  public static final class EmailRecoveryNotAllowedException extends Exception {}

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
}
