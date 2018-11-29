package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;
import it.chalmers.gamma.db.repository.PasswordResetTokenRepository;

import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public final class PasswordResetService {
    PasswordResetTokenRepository repository;

    private PasswordResetService(PasswordResetTokenRepository repository) {
        this.repository = repository;
    }


    public void addToken(ITUser user, String token) {
        setToken(new PasswordResetToken(), user, token);

    }

    /**
     * adds or edits a token that associated with a user wanting to do a password reset.
     *
     * @param passwordResetToken the token object used to create a new association
     * @param user               the user that attempted a password reset
     * @param token              the token word that is associated with the password reset
     */
    public void setToken(PasswordResetToken passwordResetToken, ITUser user, String token) {
        passwordResetToken.setItUser(user);
        passwordResetToken.setToken(token);
        this.repository.save(passwordResetToken);
    }

    public void editToken(ITUser user, String token) {
        setToken(this.repository.getTokenByItUser(user), user, token);
    }

    public boolean userHasActiveReset(ITUser user) {
        return this.repository.existsByItUser(user);
    }

    public boolean tokenMatchesUser(ITUser user, String token) {
        PasswordResetToken resetToken = this.repository.getTokenByItUser(user);
        return resetToken.getToken().equals(token);
    }

    public void removeToken(ITUser user) {
        this.repository.delete(this.repository.getTokenByItUser(user));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetService that = (PasswordResetService) o;
        return this.repository.equals(that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.repository);
    }

    @Override
    public String toString() {
        return "PasswordResetService{"
            + "repository=" + this.repository
            + '}';
    }
}