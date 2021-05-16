package it.chalmers.gamma.internal.authority.supergroup.service;

import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthoritySuperGroupService implements CreateEntity<AuthoritySuperGroupShallowDTO>, DeleteEntity<AuthoritySuperGroupPK> {

    private final AuthoritySuperGroupRepository authoritySuperGroupRepository;

    public AuthoritySuperGroupService(AuthoritySuperGroupRepository authoritySuperGroupRepository) {
        this.authoritySuperGroupRepository = authoritySuperGroupRepository;
    }

    @Override
    public void create(AuthoritySuperGroupShallowDTO authority) throws EntityAlreadyExistsException {
        try {
            this.authoritySuperGroupRepository.save(
                    new AuthoritySuperGroup(authority)
            );
        } catch (IllegalArgumentException e) {
            throw new EntityAlreadyExistsException();
        }
    }

    @Override
    public void delete(AuthoritySuperGroupPK id) throws EntityNotFoundException {
        try {
            this.authoritySuperGroupRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException();
        }
    }
}
