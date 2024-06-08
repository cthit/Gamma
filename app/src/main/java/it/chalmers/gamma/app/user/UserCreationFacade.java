package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.throttling.ThrottlingService;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.*;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserCreationFacade extends Facade {

  private static final String MAIL_POSTFIX = "chalmers.se";
  private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationFacade.class);
  private final MailService mailService;
  private final UserActivationRepository userActivationRepository;
  private final UserRepository userRepository;
  private final ThrottlingService throttlingService;

  public UserCreationFacade(
      AccessGuard accessGuard,
      MailService mailService,
      UserActivationRepository userActivationRepository,
      UserRepository userRepository,
      ThrottlingService throttlingService) {
    super(accessGuard);
    this.mailService = mailService;
    this.userActivationRepository = userActivationRepository;
    this.userRepository = userRepository;
    this.throttlingService = throttlingService;
  }

  public void tryToActivateUser(String cidRaw) {
    accessGuard.require(isNotSignedIn());

    Cid cid = new Cid(cidRaw);
    try {
      if (throttlingService.canProceed(cidRaw + "-activation", 3)) {
        UserActivationToken userActivationToken =
            this.userActivationRepository.createActivationToken(cid);
        sendEmail(cid, userActivationToken);
      } else {
        LOGGER.info("Throttling an activation and its email...");
      }
      LOGGER.info("Cid " + cid + " has been activated");
    } catch (UserActivationRepository.CidNotAllowedException e) {
      LOGGER.info("Someone tried to activate the cid: " + cid);
    }
  }

  public UUID createUser(NewUser newUser) throws EmailNotUniqueException, CidNotUniqueException {
    this.accessGuard.require(isAdmin());

    UserId userId = UserId.generate();

    try {
      this.userRepository.create(
          new GammaUser(
              userId,
              new Cid(newUser.cid),
              new Nick(newUser.nick),
              new FirstName(newUser.firstName),
              new LastName(newUser.lastName),
              new AcceptanceYear(newUser.acceptanceYear),
              Language.valueOf(newUser.language),
              new UserExtended(new Email(newUser.email), 0, false, null)),
          new UnencryptedPassword(newUser.password));

      return userId.value();
    } catch (UserRepository.CidAlreadyInUseException e) {
      throw new CidNotUniqueException();
    } catch (UserRepository.EmailAlreadyInUseException e) {
      throw new EmailNotUniqueException();
    }
  }

  @Transactional
  public void createUserWithCode(
      NewUser data, String token, String confirmPassword, boolean acceptsUserAgreement)
      throws SomePropertyNotUniqueException {
    this.accessGuard.require(isNotSignedIn());

    if (!data.password.equals(confirmPassword)) {
      throw new IllegalArgumentException("password not confirmed");
    }

    if (!acceptsUserAgreement) {
      throw new IllegalArgumentException("must accept user agreement");
    }

    Cid tokenCid = this.userActivationRepository.getByToken(new UserActivationToken(token));

    if (tokenCid.value().equals(data.cid)) {
      Cid cid = new Cid(data.cid);

      try {
        this.userRepository.create(
            new GammaUser(
                UserId.generate(),
                cid,
                new Nick(data.nick),
                new FirstName(data.firstName),
                new LastName(data.lastName),
                new AcceptanceYear(data.acceptanceYear),
                Language.valueOf(data.language),
                new UserExtended(new Email(data.email), 0, false, null)),
            new UnencryptedPassword(data.password));
      } catch (UserRepository.CidAlreadyInUseException
          | UserRepository.EmailAlreadyInUseException e) {
        throw new SomePropertyNotUniqueException();
      }

      this.userActivationRepository.removeActivation(cid);
    }
  }

  private void sendEmail(Cid cid, UserActivationToken userActivationToken) {
    String to = cid.getValue() + "@" + MAIL_POSTFIX;
    String code = userActivationToken.value();
    String message = "Your code to Gamma is: " + code;
    this.mailService.sendMail(to, "Gamma activation code", message);
  }

  public record NewUser(
      String password,
      String nick,
      String firstName,
      String lastName,
      String email,
      int acceptanceYear,
      String cid,
      String language) {}

  public static class SomePropertyNotUniqueException extends Exception {
    public SomePropertyNotUniqueException() {
      super("Please double check your details");
    }
  }

  public static class CidNotUniqueException extends Exception {
    public CidNotUniqueException() {
      super("Cid is already in use");
    }
  }

  public static class EmailNotUniqueException extends Exception {
    public EmailNotUniqueException() {
      super("Email is already in use");
    }
  }
}
