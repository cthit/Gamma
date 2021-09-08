package it.chalmers.gamma.app;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.user.UnencryptedPassword;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.app.user.PasswordResetRepository;
import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.app.user.UserSignInIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResetPasswordFacade extends Facade {

    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;

    private static Logger LOGGER = LoggerFactory.getLogger(ResetPasswordFacade.class);

    public ResetPasswordFacade(AccessGuard accessGuard,
                               MailService mailService,
                               UserRepository userRepository,
                               PasswordResetRepository passwordResetRepository) {
        super(accessGuard);
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
    }

    public void startResetPasswordProcess(UserSignInIdentifier signInIdentifier) throws PasswordResetProcessException {
        accessGuard.requireNotSignedIn();
        Optional<User> maybeUser = getUser(signInIdentifier);

        if (maybeUser.isEmpty()) {
            LOGGER.debug("Someone tried to reset the password for " + signInIdentifier + " that doesn't exist");
            throw new PasswordResetProcessException();
        }

        User user = maybeUser.get();

        PasswordResetToken token = this.passwordResetRepository.createNewToken(user);
        sendPasswordResetTokenMail(user, token);
    }

    public void finishResetPasswordProcess(UserSignInIdentifier signInIdentifier, PasswordResetToken inputToken, UnencryptedPassword newPassword) throws PasswordResetProcessException {
        accessGuard.requireNotSignedIn();
        Optional<User> maybeUser = getUser(signInIdentifier);

        if (maybeUser.isEmpty()) {
            LOGGER.debug("Someone tried to finish the reset password process for " + signInIdentifier + " that doesn't exist");
            throw new PasswordResetProcessException();
        }

        User user = maybeUser.get();
        Optional<PasswordResetToken> maybeToken = this.passwordResetRepository.getToken(user.id());

        if (maybeToken.isEmpty()) {
            LOGGER.debug("No token exists for the user " + user);
            throw new PasswordResetProcessException();
        }

        PasswordResetToken token = maybeToken.get();

        if (token.equals(inputToken)) {
            this.passwordResetRepository.removeToken(token);
            this.userRepository.setPassword(user.id(), newPassword);
        } else {
            LOGGER.debug("Incorrect password reset token for user " + user);
            throw new PasswordResetProcessException();
        }

    }

    private void sendPasswordResetTokenMail(User user, PasswordResetToken token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailService.sendMail(user.email().value(), subject, message);
    }

    private Optional<User> getUser(UserSignInIdentifier signInIdentifier) {
        Optional<User> user = Optional.empty();
        if (signInIdentifier instanceof Email email) {
            user = this.userRepository.get(email);
        } else if (signInIdentifier instanceof Cid cid) {
            user = this.userRepository.get(cid);
        }
        return user;
    }

    //Vague for security reasons
    public static class PasswordResetProcessException extends Exception {

    }
}
