package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.domain.authority.data.Authority;
import it.chalmers.gamma.domain.authority.data.AuthorityPK;
import it.chalmers.gamma.domain.authority.data.AuthorityRepository;
import it.chalmers.gamma.domain.authority.exception.AuthorityAlreadyExistsException;
import it.chalmers.gamma.domain.authority.exception.AuthorityNotFoundException;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelService;

import java.util.*;

import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityLevelService authorityLevelService;
    private final AuthorityFinder authorityFinder;

    public AuthorityService(AuthorityRepository authorityRepository,
                            AuthorityLevelService authorityLevelService,
                            AuthorityFinder authorityFinder) {
        this.authorityRepository = authorityRepository;
        this.authorityLevelService = authorityLevelService;
        this.authorityFinder = authorityFinder;
    }

    public void createAuthority(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName)
                throws AuthorityAlreadyExistsException {
        if(this.authorityFinder.authorityExists(superGroupId, postId, authorityLevelName)) {
            throw new AuthorityAlreadyExistsException();
        }

        this.authorityRepository.save(
                new Authority(
                        new AuthorityPK(superGroupId, postId, authorityLevelName.value)
                )
        );
    }

    public void removeAuthority(SuperGroupId superGroupId, PostId postId, AuthorityLevelName authorityLevelName) throws AuthorityNotFoundException {
        if (!this.authorityFinder.authorityExists(superGroupId, postId, authorityLevelName)) {
            throw new AuthorityNotFoundException();
        }

        this.authorityRepository.deleteById(new AuthorityPK(superGroupId, postId, authorityLevelName.value));
    }
}
