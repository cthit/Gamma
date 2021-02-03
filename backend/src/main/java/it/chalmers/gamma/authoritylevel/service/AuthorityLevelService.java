package it.chalmers.gamma.authoritylevel.service;

import java.util.UUID;

import it.chalmers.gamma.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.authoritylevel.controller.response.AuthorityLevelDoesNotExistResponse;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevel;
import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevelRepository;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.authoritylevel.exception.AuthorityLevelNotFoundException;
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


    public void removeAuthorityLevel(UUID authorityLevelId) throws AuthorityLevelNotFoundException {
        if(this.authorityLevelFinder.authorityLevelExists(authorityLevelId)) {
            throw new AuthorityLevelNotFoundException();
        }

        this.authorityLevelRepository.deleteById(authorityLevelId);
    }

}
