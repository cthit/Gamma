package it.chalmers.gamma.domain.authoritylevel.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.data.db.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.data.db.AuthorityLevelRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorityLevelService implements CreateEntity<AuthorityLevelName>, DeleteEntity<AuthorityLevelName> {

    private final AuthorityLevelRepository authorityLevelRepository;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository,
                                 AuthorityLevelFinder authorityLevelFinder) {
        this.authorityLevelRepository = authorityLevelRepository;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    public void create(AuthorityLevelName levelName) throws EntityAlreadyExistsException {
        if (this.authorityLevelFinder.authorityLevelExists(levelName)) {
            throw new EntityAlreadyExistsException();
        }

        this.authorityLevelRepository.save(new AuthorityLevel(levelName.value));
    }


    public void delete(AuthorityLevelName name) {
        this.authorityLevelRepository.deleteById(name.value);
    }

}
