package it.chalmers.gamma.domain.supergroup.service;

import it.chalmers.gamma.domain.*;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.data.SuperGroup;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupRepository;
import it.chalmers.gamma.domain.supergroup.service.exception.SuperGroupHasGroupsException;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupService implements CreateEntity<SuperGroupDTO>, DeleteEntity<SuperGroupId>, UpdateEntity<SuperGroupDTO> {

    private final SuperGroupRepository repository;
    private final SuperGroupFinder finder;
    private final GroupFinder groupFinder;

    public SuperGroupService(SuperGroupRepository repository,
                             SuperGroupFinder finder,
                             GroupFinder groupFinder) {
        this.repository = repository;
        this.finder = finder;
        this.groupFinder = groupFinder;
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
