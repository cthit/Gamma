package it.chalmers.gamma.internal.userpasswordreset.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.PasswordReset;
import it.chalmers.gamma.domain.PasswordResetToken;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.internal.user.service.UserService;
import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.util.TokenUtils;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetRepository repository;
    private final MailSenderService mailSenderService;
    private final UserService userService;

    public PasswordResetService(PasswordResetRepository repository,
                                MailSenderService mailSenderService,
                                UserService userService) {
        this.repository = repository;
        this.mailSenderService = mailSenderService;
        this.userService = userService;
    }

    public void handlePasswordReset(String cidOrEmail) throws UserService.UserNotFoundException {
        User user;

        try{
            user = this.userService.get(Cid.valueOf(cidOrEmail));
        }catch(UserService.UserNotFoundException e) {
            try {
                user = this.userService.get(Email.valueOf(cidOrEmail));
            } catch(UserService.UserNotFoundException e2) {
                throw new UserService.UserNotFoundException();
            }
        }

        this.handlePasswordReset(user);
    }

    public void handlePasswordReset(User user) {
        try {
            this.removeToken(user);
        } catch (UserService.UserNotFoundException ignored) { }

        PasswordResetToken token = PasswordResetToken.generate();

        this.addToken(user, token);
        this.sendMail(user, token);
    }

    public void addToken(User user, PasswordResetToken token) {
        this.repository.save(new PasswordResetEntity(
                token,
                user.id(),
                Instant.now()
        ));
    }

    //TODO: check if the token is too old
    public boolean tokenMatchesUser(UserId userId, String token) throws UserService.UserNotFoundException {
        PasswordReset d = this.repository.findById(userId)
                .orElseThrow(UserService.UserNotFoundException::new)
                .toDTO();
        return d.token().equals(token);
    }

    public void removeToken(User user) throws UserService.UserNotFoundException {
        try {
            this.repository.deleteById(user.id());
        } catch(IllegalArgumentException e) {
            throw new UserService.UserNotFoundException();
        }
    }

    private void sendMail(User user, PasswordResetToken token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.email().get(), subject, message);
    }

    protected PasswordResetEntity getPasswordResetToken(PasswordReset passwordResetDTO) {
        return this.repository.findById(passwordResetDTO.userId()).orElse(null);
    }

}