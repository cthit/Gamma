package it.chalmers.gamma.internal.authority.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class AuthorityService implements CreateEntity<AuthorityShallowDTO>, DeleteEntity<AuthorityPK> {

    private final AuthorityRepository authorityRepository;

    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public void create(AuthorityShallowDTO authority) throws EntityAlreadyExistsException {
        try {
            this.authorityRepository.save(
                    new Authority(authority)
            );
        } catch(IllegalArgumentException e) {
            throw new EntityAlreadyExistsException();
        }
    }

    public void delete(AuthorityPK id) throws EntityNotFoundException {
        try{
            this.authorityRepository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new EntityNotFoundException();
        }
    }
}
