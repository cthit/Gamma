package it.chalmers.gamma.passwordreset;

import it.chalmers.gamma.mail.MailSenderService;
import it.chalmers.gamma.user.ITUser;

import it.chalmers.gamma.domain.user.ITUserDTO;
import it.chalmers.gamma.domain.user.PasswordResetTokenDTO;
import it.chalmers.gamma.user.ITUserFinder;
import it.chalmers.gamma.user.ITUserService;
import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final MailSenderService mailSenderService;
    private final ITUserService itUserService;
    private final ITUserFinder userFinder;

    public PasswordResetService(PasswordResetTokenRepository repository,
                                MailSenderService mailSenderService,
                                ITUserService itUserService,
                                ITUserFinder userFinder) {
        this.repository = repository;
        this.mailSenderService = mailSenderService;
        this.itUserService = itUserService;
        this.userFinder = userFinder;
    }

    public void handlePasswordReset(ITUserDTO user) {

        String token = TokenUtils.generateToken(10,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS);
        if (this.userHasActiveReset(user)) {
            this.editToken(user, token);
        } else {
            this.addToken(user, token);
        }
        this.sendMail(user, token);
    }


    public void addToken(ITUserDTO user, String token) {
        setToken(new PasswordResetToken(), user, token);

    }

    /**
     * adds or edits a token that associated with a user wanting to do a password reset.
     *
     * @param passwordResetToken the token object used to create a new association
     * @param userDTO               the user that attempted a password reset
     * @param token              the token word that is associated with the password reset
     */
    private void setToken(PasswordResetToken passwordResetToken, ITUserDTO userDTO, String token) {
        ITUser user = this.userFinder.getUserEntity(userDTO);
        passwordResetToken.setItUser(user);
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public void editToken(ITUserDTO userDTO, String token) {
        ITUser user = this.userFinder.getUserEntity(userDTO);
        setToken(Objects.requireNonNull(this.repository.findByItUser(user).orElse(null)), user.toDTO(), token);
    }

    public boolean userHasActiveReset(ITUserDTO user) {
        return this.repository.existsByItUser(this.userFinder.getUserEntity(user));
    }

    public boolean tokenMatchesUser(ITUserDTO user, String token) {
        PasswordResetToken storedToken = this.repository.findByItUser(this.userFinder.getUserEntity(user))
                .orElse(null);
        return storedToken != null && storedToken.getToken().equals(token);
    }

    public void removeToken(ITUserDTO user) {
        this.repository.delete(Objects.requireNonNull(
                this.repository.findByItUser(this.userFinder.getUserEntity(user)).orElse(null)));
    }


    // TODO Make sure that an URL is added to the email
    private void sendMail(ITUserDTO user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.getEmail(), subject, message);
    }

    protected PasswordResetToken getPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO) {
        return this.repository.findById(passwordResetTokenDTO.getId()).orElse(null);
    }

}