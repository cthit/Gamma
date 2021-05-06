package it.chalmers.gamma.internal.authoritylevel.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class AuthorityLevelService implements CreateEntity<AuthorityLevelName>, DeleteEntity<AuthorityLevelName> {

    private final AuthorityLevelRepository authorityLevelRepository;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository) {
        this.authorityLevelRepository = authorityLevelRepository;
    }

    public void create(AuthorityLevelName levelName) throws EntityAlreadyExistsException {
        try {
            this.authorityLevelRepository.save(new AuthorityLevel(levelName));
        } catch(IllegalArgumentException e) {
            throw new EntityAlreadyExistsException();
        }
    }

    public void delete(AuthorityLevelName name) {
        this.authorityLevelRepository.deleteById(name.value);
    }

}
