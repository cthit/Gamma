package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.activation.domain.UserActivationToken;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.whitelist.WhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static it.chalmers.gamma.app.authentication.AccessGuard.isAdmin;
import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

@Service
public class UserCreationFacade extends Facade {

    private final MailService mailService;
    private final WhitelistRepository whitelistRepository;
    private final UserActivationRepository userActivationRepository;
    private final UserRepository userRepository;

    private static final String MAIL_POSTFIX = "student.chalmers.se";

    private static Logger LOGGER = LoggerFactory.getLogger(UserCreationFacade.class);

    public UserCreationFacade(AccessGuard accessGuard,
                              MailService mailService,
                              WhitelistRepository whitelistRepository,
                              UserActivationRepository userActivationRepository,
                              UserRepository userRepository) {
        super(accessGuard);
        this.mailService = mailService;
        this.whitelistRepository = whitelistRepository;
        this.userActivationRepository = userActivationRepository;
        this.userRepository = userRepository;
    }

    public void tryToActivateUser(String cidRaw) {
        accessGuard.require(isNotSignedIn());

        Cid cid = new Cid(cidRaw);
        try {
            UserActivationToken userActivationToken = this.userActivationRepository.createActivationToken(cid);
            sendEmail(cid, userActivationToken);
            LOGGER.info("Cid " + cid + " has been activated");
        } catch (UserActivationRepository.CidNotWhitelistedException e) {
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
        this.accessGuard.require(isAdmin());

        this.userRepository.create(
                new User(
                        UserId.generate(),
                        new Cid(newUser.cid),
                        new Nick(newUser.nick),
                        new FirstName(newUser.firstName),
                        new LastName(newUser.lastName),
                        new AcceptanceYear(newUser.acceptanceYear),
                        Language.valueOf(newUser.language),
                        new UserExtended(
                                new Email(newUser.email),
                                0,
                                false,
                                false,
                                false,
                                null
                        )),
                new UnencryptedPassword(newUser.password)
        );
    }

    @Transactional
    public void createUserWithCode(NewUser data, String token) {
        this.accessGuard.require(isNotSignedIn());

        Cid tokenCid = this.userActivationRepository.getByToken(new UserActivationToken(token));

        //TODO: Check if email is not student@chalmers.se

        if (tokenCid.value().equals(data.cid)) {
            Cid cid = new Cid(data.cid);

            this.userRepository.create(
                    new User(
                            UserId.generate(),
                            cid,
                            new Nick(data.nick),
                            new FirstName(data.firstName),
                            new LastName(data.lastName),
                            new AcceptanceYear(data.acceptanceYear),
                            Language.valueOf(data.language),
                            new UserExtended(
                                    new Email(data.email),
                                    0,
                                    false,
                                    false,
                                    false,
                                    null
                            )
                    ),
                    new UnencryptedPassword(data.password)
            );

            this.userActivationRepository.removeActivation(cid);
        }
    }

    private void sendEmail(Cid cid, UserActivationToken userActivationToken) {
        String to = cid.getValue() + "@" + MAIL_POSTFIX;
        String code = userActivationToken.value();
        String message = "Your code to Gamma is: " + code;
        this.mailService.sendMail(to, "Gamma activation code", message);
    }
}
