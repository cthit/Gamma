package it.chalmers.gamma.domain.passwordreset.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.mail.MailSenderService;

import it.chalmers.gamma.domain.passwordreset.data.PasswordResetToken;
import it.chalmers.gamma.domain.passwordreset.data.PasswordResetTokenDTO;
import it.chalmers.gamma.domain.passwordreset.data.PasswordResetTokenRepository;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.service.UserFinder;
import it.chalmers.gamma.util.TokenUtils;

import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository repository;
    private final MailSenderService mailSenderService;
    private final UserFinder userFinder;

    public PasswordResetService(PasswordResetTokenRepository repository,
                                MailSenderService mailSenderService,
                                UserFinder userFinder) {
        this.repository = repository;
        this.mailSenderService = mailSenderService;
        this.userFinder = userFinder;
    }

    public void handlePasswordReset(String cidOrEmail) throws EntityNotFoundException {
        UserDTO user;

        try{
            user = this.userFinder.get(new Cid(cidOrEmail));
        }catch(EntityNotFoundException e) {
            try {
                user = this.userFinder.get(new Email(cidOrEmail));
            } catch(EntityNotFoundException e2) {
                throw new EntityNotFoundException();
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
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }

        this.addToken(user, token);
        this.sendMail(user, token);
    }


    public void addToken(UserDTO user, String token) {
        setToken(new PasswordResetToken(), user, token);

    }

    private void setToken(PasswordResetToken passwordResetToken, UserDTO user, String token) {
        passwordResetToken.setUserId(user.getId());
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public boolean tokenMatchesUser(UserId userId, String token) {
        Optional<PasswordResetToken> storedToken = this.repository.findById(userId);
        return storedToken.isPresent() && storedToken.get().getToken().equals(token);
    }

    public void removeToken(UserDTO user) throws EntityNotFoundException {
        try {

            this.repository.deleteById(user.getId());
        } catch(EmptyResultDataAccessException e) {
            throw new EntityNotFoundException();
        }
    }

    private void sendMail(UserDTO user, String token) {
        String subject = "Password reset for Account at IT division of Chalmers";
        String message = "A password reset have been requested for this account, if you have not requested "
                + "this mail, feel free to ignore it. \n Your reset code : " + token;
        this.mailSenderService.trySendingMail(user.getEmail().get(), subject, message);
    }

    protected PasswordResetToken getPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO) {
        return this.repository.findById(passwordResetTokenDTO.getUserId()).orElse(null);
    }

}