package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.PasswordResetToken;
import it.chalmers.gamma.db.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    PasswordResetTokenRepository repository;

    public PasswordResetService(PasswordResetTokenRepository repository){
        this.repository = repository;
    }


    public void addToken(ITUser user, String token){
        setToken(new PasswordResetToken(),user, token);

    }

    public void setToken(PasswordResetToken passwordResetToken, ITUser user, String token){
        passwordResetToken.setItUser(user);
        passwordResetToken.setToken(token);
        repository.save(passwordResetToken);
    }

    public void editToken(ITUser user, String token){
        setToken(repository.getTokenByItUser(user), user, token);
    }

    public boolean userHasActiveReset(ITUser user){
        return repository.existsByItUser(user);
    }
    public boolean tokenMatchesUser(ITUser user, String token){
        PasswordResetToken resetToken = repository.getTokenByItUser(user);
        return resetToken.getToken().equals(token);
    }
    public void removeToken(ITUser user){
        repository.delete(repository.getTokenByItUser(user));
    }
}