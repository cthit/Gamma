package it.chalmers.gamma.adapter.secondary.jpa.user;

import it.chalmers.gamma.app.port.repository.UserActivationRepository;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.useractivation.UserActivation;
import it.chalmers.gamma.app.domain.useractivation.UserActivationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivationRepositoryAdapter implements UserActivationRepository {

    @Override
    public UserActivationToken createUserActivationCode(Cid cid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserActivation> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cid getByToken(UserActivationToken token) {
        throw new UnsupportedOperationException();
    }
}
