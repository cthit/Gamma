package it.chalmers.gamma.internal.user.passwordreset.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.util.TokenUtils;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final MailSenderService mailSenderService;
    private final UserService userService;

    public PasswordResetService(PasswordResetTokenRepository repository,
                                MailSenderService mailSenderService,
                                UserService userService) {
        this.repository = repository;
        this.mailSenderService = mailSenderService;
        this.userService = userService;
    }

    public void handlePasswordReset(String cidOrEmail) throws UserService.UserNotFoundException {
        UserDTO user;

        try{
            user = this.userService.get(new Cid(cidOrEmail));
        }catch(UserService.UserNotFoundException e) {
            try {
                user = this.userService.get(new Email(cidOrEmail));
            } catch(UserService.UserNotFoundException e2) {
                throw new UserService.UserNotFoundException();
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

        try {
            this.removeToken(user);
        } catch (UserService.UserNotFoundException ignored) { }

        this.addToken(user, token);
        this.sendMail(user, token);
    }

    public void addToken(UserDTO user, String token) {
        this.repository.save(new PasswordResetTokenEntity(
                token,
                user.id(),
                Instant.now()
        ));
    }

    public boolean tokenMatchesUser(UserId userId, String token) throws UserService.UserNotFoundException {
        PasswordResetTokenDTO d = this.repository.findById(userId)
                .orElseThrow(UserService.UserNotFoundException::new)
                .toDTO();
        return d.token().equals(token);
    }

    public void removeToken(UserDTO user) throws UserService.UserNotFoundException {
        try {
            this.repository.deleteById(user.id());
        } catch(IllegalArgumentException e) {
            throw new UserService.UserNotFoundException();
        }
    }

    private void sendMail(UserDTO user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.email().get(), subject, message);
    }

    protected PasswordResetTokenEntity getPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO) {
        return this.repository.findById(passwordResetTokenDTO.userId()).orElse(null);
    }

}