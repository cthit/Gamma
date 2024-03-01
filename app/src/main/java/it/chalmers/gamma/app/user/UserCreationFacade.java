package it.chalmers.gamma.app.user;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
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

  private static final String MAIL_POSTFIX = "student.chalmers.se";
  private static final Logger LOGGER = LoggerFactory.getLogger(UserCreationFacade.class);
  private final MailService mailService;
  private final UserActivationRepository userActivationRepository;
  private final UserRepository userRepository;

  public UserCreationFacade(
      AccessGuard accessGuard,
      MailService mailService,
      UserActivationRepository userActivationRepository,
      UserRepository userRepository) {
    super(accessGuard);
    this.mailService = mailService;
    this.userActivationRepository = userActivationRepository;
    this.userRepository = userRepository;
  }

  public void tryToActivateUser(String cidRaw) {
    accessGuard.require(isNotSignedIn());

    Cid cid = new Cid(cidRaw);
    try {
      UserActivationToken userActivationToken =
          this.userActivationRepository.createActivationToken(cid);
      sendEmail(cid, userActivationToken);
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
  public void createUserWithCode(NewUser data, String token) throws SomePropertyNotUniqueException {
    this.accessGuard.require(isNotSignedIn());

    Cid tokenCid = this.userActivationRepository.getByToken(new UserActivationToken(token));

    // TODO: Check if email is not student@chalmers.se

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
