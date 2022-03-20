package it.chalmers.gamma.app.user.passwordreset;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.mail.domain.MailService;
import it.chalmers.gamma.app.user.FindUserByIdentifier;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetRepository;
import it.chalmers.gamma.app.user.passwordreset.domain.PasswordResetToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.chalmers.gamma.app.authentication.AccessGuard.isNotSignedIn;

@Service
public class UserResetPasswordFacade extends Facade {

    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final FindUserByIdentifier findUserByIdentifier;

    private static Logger LOGGER = LoggerFactory.getLogger(UserResetPasswordFacade.class);

    public UserResetPasswordFacade(AccessGuard accessGuard,
                                   MailService mailService,
                                   UserRepository userRepository,
                                   PasswordResetRepository passwordResetRepository,
                                   FindUserByIdentifier findUserByIdentifier) {
        super(accessGuard);
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.findUserByIdentifier = findUserByIdentifier;
    }

    public void startResetPasswordProcess(String userIdentifier) throws PasswordResetProcessException {
        this.accessGuard.require(isNotSignedIn());

        Optional<GammaUser> maybeUser = findUserByIdentifier.toUser(userIdentifier);

        if (maybeUser.isEmpty()) {
            LOGGER.debug("Someone tried to reset the value for " + userIdentifier + " that doesn't exist");
            throw new PasswordResetProcessException();
        }

        GammaUser user = maybeUser.get();

        PasswordResetToken token = this.passwordResetRepository.createNewToken(user);
        sendPasswordResetTokenMail(user, token);
    }

    public void finishResetPasswordProcess(String userIdentifier, String inputTokenRaw, String newPassword) throws PasswordResetProcessException {
        this.accessGuard.require(isNotSignedIn());

        Optional<GammaUser> maybeUser = findUserByIdentifier.toUser(userIdentifier);

        if (maybeUser.isEmpty()) {
            LOGGER.debug("Someone tried to finish the reset value process for " + userIdentifier + " that doesn't exist");
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

    private void sendPasswordResetTokenMail(GammaUser user, PasswordResetToken token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A value reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token.value();
        this.mailService.sendMail(user.extended().email().value(), subject, message);
    }

    //Vague for security reasons
    public static class PasswordResetProcessException extends Exception {

    }
}
