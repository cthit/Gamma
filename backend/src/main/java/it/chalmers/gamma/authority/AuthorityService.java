package it.chalmers.gamma.authority;

import it.chalmers.gamma.authority.exception.AuthorityAlreadyExists;
import it.chalmers.gamma.authority.exception.AuthorityNotFoundException;
import it.chalmers.gamma.authoritylevel.data.AuthorityLevel;
import it.chalmers.gamma.authoritylevel.dto.AuthorityLevelDTO;
import it.chalmers.gamma.authoritylevel.service.AuthorityLevelService;
import it.chalmers.gamma.supergroup.SuperGroupDTO;

import it.chalmers.gamma.post.PostDTO;

import java.util.*;

import javax.transaction.Transactional;

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

    public void createAuthority(String superGroupName, String postName, String authorityLevelName)
                throws AuthorityAlreadyExists {
        Optional<Authority> maybeAuthority = this.authorityFinder.getAuthority(superGroupName, postName);

        if(maybeAuthority.isPresent()) {
            throw new AuthorityAlreadyExists();
        }

        AuthorityLevel authorityLevelEntity = this.authorityLevelService.getAuthorityLevel(authorityLevel);
        Authority authority = new Authority(
                new AuthorityPK(
                        superGroup.getId(),
                        post.getId()
                ),
                authorityLevelEntity
        );

        this.authorityRepository.save(authority);
    }

    public void removeAuthority(SuperGroupDTO superGroup, PostDTO post) throws AuthorityNotFoundException {
        Optional<Authority> maybeAuthority = this.authorityFinder.getAuthority(superGroup, post);
        Authority authority = maybeAuthority.orElseThrow(AuthorityNotFoundException::new);

        this.authorityRepository.delete(authority);
    }

    @Transactional
    public void removeAuthority(UUID id) {
        this.authorityRepository.deleteByInternalId(id);
    }

    @Transactional
    public void removeAllAuthoritiesWithAuthorityLevel(AuthorityLevelDTO authorityLevelDTO) {
        List<AuthorityDTO> authorities = this.authorityFinder.getAllAuthoritiesWithAuthorityLevel(authorityLevelDTO);
        authorities.forEach(a -> this.removeAuthority(a.getId()));
    }
}
