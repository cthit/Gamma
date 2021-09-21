package it.chalmers.gamma.app.user;

import it.chalmers.gamma.app.AccessGuard;
import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.mail.MailService;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.UnencryptedPassword;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.useractivation.UserActivation;
import it.chalmers.gamma.domain.useractivation.UserActivationToken;
import it.chalmers.gamma.app.whitelist.WhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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
                              UserActivationRepository userActivationRepository, UserRepository userRepository) {
        super(accessGuard);
        this.mailService = mailService;
        this.whitelistRepository = whitelistRepository;
        this.userActivationRepository = userActivationRepository;
        this.userRepository = userRepository;
    }

    public void tryToActivateUser(Cid cid) {
        accessGuard.requireNotSignedIn();
        if (this.whitelistRepository.isWhitelisted(cid)) {
            UserActivationToken userActivationToken = this.userActivationRepository.createUserActivationCode(cid);
            sendEmail(cid, userActivationToken);
            LOGGER.info("Cid " + cid + " has been activated");
        } else {
            LOGGER.info("Someone tried to activate the cid: " + cid);
        }
    }

    public void createUserWithCode(User newUser, UnencryptedPassword password, UserActivationToken token) {

    }

    public void createUser(User newUser) {
        this.userRepository.create(newUser);
    }

    public void removeUserActivation(Cid cid) {

    }

    public List<UserActivation> getAllUserActivations() {
        return this.userActivationRepository.getAll();
    }

    private void sendEmail(Cid cid, UserActivationToken userActivationToken) {
        String to = cid.getValue() + "@" + MAIL_POSTFIX;
        String code = userActivationToken.value();
        String message = "Your code to Gamma is: " + code;
        this.mailService.sendMail(to, "Gamma activation code", message);
    }
}
