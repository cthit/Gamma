package it.chalmers.gamma.internal.authority.post.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;

import org.springframework.stereotype.Service;

@Service
public class AuthorityPostService implements CreateEntity<AuthorityPostShallowDTO>, DeleteEntity<AuthorityPostPK> {

    private final AuthorityPostRepository authorityPostRepository;

    public AuthorityPostService(AuthorityPostRepository authorityPostRepository) {
        this.authorityPostRepository = authorityPostRepository;
    }

    public void create(AuthorityPostShallowDTO authority) throws EntityAlreadyExistsException {
        try {
            this.authorityPostRepository.save(
                    new AuthorityPost(authority)
            );
        } catch(IllegalArgumentException e) {
            throw new EntityAlreadyExistsException();
        }
    }

    public void delete(AuthorityPostPK id) throws EntityNotFoundException {
        try{
            this.authorityPostRepository.deleteById(id);
        } catch(IllegalArgumentException e){
            throw new EntityNotFoundException();
        }
    }
}
