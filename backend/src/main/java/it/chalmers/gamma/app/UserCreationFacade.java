package it.chalmers.gamma.app;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.UserActivation;
import it.chalmers.gamma.app.domain.UserActivationToken;
import it.chalmers.gamma.app.user.UserActivationRepository;
import it.chalmers.gamma.app.whitelist.WhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserCreationFacade extends Facade {

    private final MailService mailService;
    private final WhitelistRepository whitelistRepository;
    private final UserActivationRepository userActivationRepository;

    private static final String MAIL_POSTFIX = "student.chalmers.se";

    private static Logger LOGGER = LoggerFactory.getLogger(UserCreationFacade.class);

    public UserCreationFacade(AccessGuard accessGuard,
                              MailService mailService,
                              WhitelistRepository whitelistRepository,
                              UserActivationRepository userActivationRepository) {
        super(accessGuard);
        this.mailService = mailService;
        this.whitelistRepository = whitelistRepository;
        this.userActivationRepository = userActivationRepository;
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

    public void removeUserActivation(Cid cid) {

    }

    public List<UserActivation> getAllUserActivations() {
        return this.userActivationRepository.getAll();
    }

    private void sendEmail(Cid cid, UserActivationToken userActivationToken) {
        String to = cid.value() + "@" + MAIL_POSTFIX;
        String code = userActivationToken.value();
        String message = "Your code to Gamma is: " + code;
        this.mailService.sendMail(to, "Gamma activation code", message);
    }
}
