package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;
import it.chalmers.gamma.db.repository.PasswordResetTokenRepository;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.user.PasswordResetTokenDTO;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository repository;
    private final DTOToEntityService dtoToEntityService;

    public PasswordResetService(PasswordResetTokenRepository repository, DTOToEntityService dtoToEntityService) {
        this.repository = repository;
        this.dtoToEntityService = dtoToEntityService;
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
        PasswordResetToken storedToken = this.repository.findByItUser(this.dtoToEntityService.fromDTO(user)).orElse(null);
        return storedToken != null && storedToken.getToken().equals(token);
    }

    public void removeToken(ITUserDTO user) {
        this.repository.delete(Objects.requireNonNull(
                this.repository.findByItUser(this.dtoToEntityService.fromDTO(user)).orElse(null)));
    }

    protected PasswordResetToken getPasswordResetToken(PasswordResetTokenDTO passwordResetTokenDTO) {
        return this.repository.findById(passwordResetTokenDTO.getId()).orElse(null);
    }

}