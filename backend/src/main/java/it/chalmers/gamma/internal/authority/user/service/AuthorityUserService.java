package it.chalmers.gamma.internal.authority.user.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorityUserService implements CreateEntity<AuthorityUserShallowDTO>, DeleteEntity<AuthorityUserPK> {

    private final AuthorityUserRepository authorityUserRepository;

    public AuthorityUserService(AuthorityUserRepository authorityUserRepository) {
        this.authorityUserRepository = authorityUserRepository;
    }

    @Override
    public void create(AuthorityUserShallowDTO authority) throws EntityAlreadyExistsException {
        try {
            this.authorityUserRepository.save(
                    new AuthorityUserEntity(authority)
            );
        } catch (IllegalArgumentException e) {
            throw new EntityAlreadyExistsException();
        }
    }

    @Override
    public void delete(AuthorityUserPK id) throws EntityNotFoundException {
        try {
            this.authorityUserRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException();
        }
    }
}
