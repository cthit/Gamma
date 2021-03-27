package it.chalmers.gamma.domain.authority.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.authority.data.db.Authority;
import it.chalmers.gamma.domain.authority.data.db.AuthorityPK;
import it.chalmers.gamma.domain.authority.data.db.AuthorityRepository;
import it.chalmers.gamma.domain.authority.data.dto.AuthorityShallowDTO;

import org.springframework.stereotype.Service;

@Service
public class AuthorityService implements CreateEntity<AuthorityShallowDTO>, DeleteEntity<AuthorityPK> {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public void create(AuthorityShallowDTO authority) throws EntityAlreadyExistsException {
        this.authorityRepository.save(
                new Authority(
                        new AuthorityPK(
                                authority.getSuperGroupId(),
                                authority.getPostId(),
                                authority.getAuthorityLevelName()
                        )
                )
        );
    }

    public void delete(AuthorityPK id) throws EntityNotFoundException {
        this.authorityRepository.deleteById(id);
    }
}
