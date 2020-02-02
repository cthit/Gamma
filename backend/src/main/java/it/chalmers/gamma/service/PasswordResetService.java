package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;
import it.chalmers.gamma.db.repository.PasswordResetTokenRepository;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.PasswordResetTokenDTO;
import it.chalmers.gamma.util.TokenUtils;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository repository;
    private final DTOToEntityService dtoToEntityService;
    private final MailSenderService mailSenderService;

    public PasswordResetService(PasswordResetTokenRepository repository, DTOToEntityService dtoToEntityService,
                                MailSenderService mailSenderService) {
        this.repository = repository;
        this.dtoToEntityService = dtoToEntityService;
        this.mailSenderService = mailSenderService;
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
        ITUser user = this.dtoToEntityService.fromDTO(userDTO);
        passwordResetToken.setItUser(user);
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public void editToken(ITUserDTO userDTO, String token) {
        ITUser user = this.dtoToEntityService.fromDTO(userDTO);
        setToken(Objects.requireNonNull(this.repository.findByItUser(user).orElse(null)), user.toDTO(), token);
    }

    public boolean userHasActiveReset(ITUserDTO user) {
        return this.repository.existsByItUser(this.dtoToEntityService.fromDTO(user));
    }

    public boolean tokenMatchesUser(ITUserDTO user, String token) {
        PasswordResetToken storedToken = this.repository.findByItUser(this.dtoToEntityService.fromDTO(user))
                .orElse(null);
        return storedToken != null && storedToken.getToken().equals(token);
    }

    public void removeToken(ITUserDTO user) {
        this.repository.delete(Objects.requireNonNull(
                this.repository.findByItUser(this.dtoToEntityService.fromDTO(user)).orElse(null)));
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