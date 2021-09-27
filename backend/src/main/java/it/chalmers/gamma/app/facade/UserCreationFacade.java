package it.chalmers.gamma.app.facade;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.port.service.PasswordService;
import it.chalmers.gamma.app.port.repository.UserActivationRepository;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.port.service.MailService;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.user.AcceptanceYear;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.FirstName;
import it.chalmers.gamma.app.domain.user.Language;
import it.chalmers.gamma.app.domain.user.LastName;
import it.chalmers.gamma.app.domain.user.Nick;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.domain.useractivation.UserActivationToken;
import it.chalmers.gamma.app.port.repository.WhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserCreationFacade extends Facade {

    private final MailService mailService;
    private final WhitelistRepository whitelistRepository;
    private final UserActivationRepository userActivationRepository;
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    private static final String MAIL_POSTFIX = "student.chalmers.se";

    private static Logger LOGGER = LoggerFactory.getLogger(UserCreationFacade.class);

    public UserCreationFacade(AccessGuard accessGuard,
                              MailService mailService,
                              WhitelistRepository whitelistRepository,
                              UserActivationRepository userActivationRepository,
                              UserRepository userRepository,
                              PasswordService passwordService) {
        super(accessGuard);
        this.mailService = mailService;
        this.whitelistRepository = whitelistRepository;
        this.userActivationRepository = userActivationRepository;
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public void tryToActivateUser(String cidRaw) {
        accessGuard.requireNotSignedIn();
        Cid cid = new Cid(cidRaw);
        if (this.whitelistRepository.isWhitelisted(cid)) {
            UserActivationToken userActivationToken = this.userActivationRepository.createUserActivationCode(cid);
            sendEmail(cid, userActivationToken);
            LOGGER.info("Cid " + cid + " has been activated");
        } else {
            LOGGER.info("Someone tried to activate the cid: " + cid);
        }
    }

    public record NewUser(String password,
                                  String nick,
                                  String firstName,
                                  String email,
                                  String lastName,
                                  int acceptanceYear,
                                  String cid,
                                  String language) { }

    public void createUser(NewUser newUser) {
        this.userRepository.create(
                new User(
                        UserId.generate(),
                        new Cid(newUser.cid),
                        new Email(newUser.email),
                        Language.valueOf(newUser.language),
                        new Nick(newUser.nick),
                        this.passwordService.encrypt(new UnencryptedPassword(newUser.password)),
                        new FirstName(newUser.firstName),
                        new LastName(newUser.lastName),
                        Instant.ofEpochSecond(0),
                        new AcceptanceYear(newUser.acceptanceYear),
                        false,
                        false,
                        ImageUri.nothing()
                )
        );
    }

    public void createUserWithCode(NewUser data, String token) {
        this.accessGuard.requireNotSignedIn();
        Cid tokenCid = this.userActivationRepository.getByToken(new UserActivationToken(token));

        //TODO: Check if email is not student@chalmers.se

        if (tokenCid.value().equals(data.cid)) {
            this.userRepository.create(
                    new User(
                            UserId.generate(),
                            new Cid(data.cid),
                            new Email(data.email),
                            Language.valueOf(data.language),
                            new Nick(data.nick),
                            this.passwordService.encrypt(new UnencryptedPassword(data.password)),
                            new FirstName(data.firstName),
                            new LastName(data.lastName),
                            Instant.now(),
                            new AcceptanceYear(data.acceptanceYear),
                            false,
                            false,
                            ImageUri.nothing()
                    )
            );
        }
    }

    public void removeUserActivation(String cid) {
        throw new UnsupportedOperationException();
    }

    public record UserActivationDTO(String cid,
                                    String token,
                                    Instant createdAt) {
        public UserActivationDTO(UserActivation userActivation) {
            this(userActivation.cid().value(),
                    userActivation.token().value(),
                    userActivation.createdAt());
        }
    }

    public List<UserActivationDTO> getAllUserActivations() {
        return this.userActivationRepository.getAll()
                .stream()
                .map(UserActivationDTO::new)
                .toList();
    }

    private void sendEmail(Cid cid, UserActivationToken userActivationToken) {
        String to = cid.getValue() + "@" + MAIL_POSTFIX;
        String code = userActivationToken.value();
        String message = "Your code to Gamma is: " + code;
        this.mailService.sendMail(to, "Gamma activation code", message);
    }
}
