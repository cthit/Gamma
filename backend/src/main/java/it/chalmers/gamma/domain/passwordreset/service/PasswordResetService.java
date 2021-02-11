package it.chalmers.gamma.domain.passwordreset.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.domain.passwordreset.data.PasswordResetToken;
import it.chalmers.gamma.domain.passwordreset.data.PasswordResetTokenDTO;
import it.chalmers.gamma.domain.passwordreset.data.PasswordResetTokenRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.domain.user.service.UserService;
import it.chalmers.gamma.util.TokenUtils;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final MailSenderService mailSenderService;
    private final UserService userService;
    private final UserFinder userFinder;

    public PasswordResetService(PasswordResetTokenRepository repository,
                                MailSenderService mailSenderService,
                                UserService userService,
                                UserFinder userFinder) {
        this.repository = repository;
        this.mailSenderService = mailSenderService;
        this.userService = userService;
        this.userFinder = userFinder;
    }

    public void handlePasswordReset(String cidOrEmail) throws UserNotFoundException {
        UserDTO user;

        try{
            user = this.userFinder.getUser(new Cid(cidOrEmail));
        }catch(UserNotFoundException e) {
            try {
                user = this.userFinder.getUser(new Email(cidOrEmail));
            } catch(UserNotFoundException e2) {
                throw new UserNotFoundException();
            }
        }

        this.handlePasswordReset(user);
    }

    public void handlePasswordReset(UserDTO user) {
        String token = TokenUtils.generateToken(
                10,
                TokenUtils.CharacterTypes.UPPERCASE,
                TokenUtils.CharacterTypes.NUMBERS
        );

        this.removeToken(user);

        this.addToken(user, token);
        this.sendMail(user, token);
    }


    public void addToken(UserDTO user, String token) {
        setToken(new PasswordResetToken(), user, token);

    }

    /**
     * adds or edits a token that associated with a user wanting to do a password reset.
     *
     * @param passwordResetToken the token object used to create a new association
     * @param userDTO               the user that attempted a password reset
     * @param token              the token word that is associated with the password reset
     */
    private void setToken(PasswordResetToken passwordResetToken, UserDTO user, String token) {
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public boolean tokenMatchesUser(UUID userId, String token) {
        Optional<PasswordResetToken> storedToken = this.repository.findByUserId(userId);
        return storedToken.isPresent() && storedToken.get().getToken().equals(token);
    }

    public void removeToken(UserDTO user) {
        this.repository.deleteById(user.getId());
    }


    // TODO Make sure that an URL is added to the email
    private void sendMail(UserDTO user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.getEmail().value, subject, message);
    }

    protected PasswordResetToken getPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO) {
        return this.repository.findById(passwordResetTokenDTO.getId()).orElse(null);
    }

}