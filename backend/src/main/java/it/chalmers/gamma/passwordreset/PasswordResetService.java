package it.chalmers.gamma.passwordreset;

import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.user.ITUserDTO;
import it.chalmers.gamma.user.ITUserFinder;
import it.chalmers.gamma.user.ITUserService;
import it.chalmers.gamma.util.TokenUtils;

import java.util.Optional;

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

        String token = TokenUtils.generateToken(
                10,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );

        if (this.userHasActiveReset(user)) {
            this.removeToken(user);
        }

        this.addToken(user, token);
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
    private void setToken(PasswordResetToken passwordResetToken, ITUserDTO user, String token) {
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public boolean userHasActiveReset(ITUserDTO user) {
        return this.repository.existsByUserId(user.getId());
    }

    public boolean tokenMatchesUser(ITUserDTO user, String token) {
        Optional<PasswordResetToken> storedToken = this.repository.findByUserId(user.getId());
        return storedToken.isPresent() && storedToken.get().getToken().equals(token);
    }

    public void removeToken(ITUserDTO user) {
        this.repository.delete(this.repository.findByUserId(user.getId()).orElseThrow());
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