package it.chalmers.gamma.app.user;

import it.chalmers.gamma.adapter.secondary.jpa.user.UserPasswordResetEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserPasswordResetRepository;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.PasswordReset;
import it.chalmers.gamma.app.domain.PasswordResetToken;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.user.UserService;
import it.chalmers.gamma.adapter.secondary.mail.MailSenderService;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class UserPasswordResetService {

    private final UserPasswordResetRepository repository;
    private final MailSenderService mailSenderService;
    private final UserService userService;

    public UserPasswordResetService(UserPasswordResetRepository repository,
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
                user = this.userService.get(new Email(cidOrEmail));
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
        this.repository.save(new UserPasswordResetEntity(
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
        this.mailSenderService.trySendingMail(user.email().value(), subject, message);
    }

    protected UserPasswordResetEntity getPasswordResetToken(PasswordReset passwordResetDTO) {
        return this.repository.findById(passwordResetDTO.userId()).orElse(null);
    }

}