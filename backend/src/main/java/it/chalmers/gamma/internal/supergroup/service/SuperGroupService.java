package it.chalmers.gamma.internal.supergroup.service;

import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.util.domain.abstraction.CreateEntity;
import it.chalmers.gamma.util.domain.abstraction.DeleteEntity;
import it.chalmers.gamma.util.domain.abstraction.UpdateEntity;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupService implements CreateEntity<SuperGroupDTO>, DeleteEntity<SuperGroupId>, UpdateEntity<SuperGroupDTO> {

    private final SuperGroupRepository repository;
    private final SuperGroupFinder finder;

    public SuperGroupService(SuperGroupRepository repository,
                             SuperGroupFinder finder,
                             GroupFinder groupFinder) {
        this.repository = repository;
        this.finder = finder;
    }

    public void create(SuperGroupDTO superGroupDTO) throws EntityAlreadyExistsException {
        this.repository.save(new SuperGroup(superGroupDTO));
    }

    public void delete(SuperGroupId id) {
        this.repository.deleteById(id);
    }

    public void update(SuperGroupDTO newSuperGroup) throws EntityNotFoundException {
        SuperGroup superGroup = this.finder.getEntity(newSuperGroup);
        superGroup.apply(newSuperGroup);
        this.repository.save(superGroup);
    }
}
