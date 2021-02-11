package it.chalmers.gamma.domain.authoritylevel.service;

import java.util.UUID;

import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.data.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorityLevelService {

    private final AuthorityLevelRepository authorityLevelRepository;
    private final AuthorityLevelFinder authorityLevelFinder;

    public AuthorityLevelService(AuthorityLevelRepository authorityLevelRepository,
                                 AuthorityLevelFinder authorityLevelFinder) {
        this.authorityLevelRepository = authorityLevelRepository;
        this.authorityLevelFinder = authorityLevelFinder;
    }

    public void addAuthorityLevel(AuthorityLevelName levelName) throws AuthorityLevelAlreadyExistsException {
        if (this.authorityLevelFinder.authorityLevelExists(levelName)) {
            throw new AuthorityLevelAlreadyExistsException();
        }

        this.authorityLevelRepository.save(new AuthorityLevel(levelName.value));
    }


    public void removeAuthorityLevel(AuthorityLevelName name) throws AuthorityLevelNotFoundException {
        if(this.authorityLevelFinder.authorityLevelExists(name)) {
            throw new AuthorityLevelNotFoundException();
        }

        this.authorityLevelRepository.deleteById(name.value);
    }

}
