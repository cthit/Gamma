package it.chalmers.gamma.domain.supergroup.service;

import java.util.UUID;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.data.SuperGroup;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupRepository;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupAlreadyExistsException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupHasGroupsException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SuperGroupService {

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

    public void createSuperGroup(SuperGroupDTO superGroupDTO) throws SuperGroupAlreadyExistsException {
        if(this.finder.superGroupExistsByName(superGroupDTO.getName())) {
            throw new SuperGroupAlreadyExistsException();
        }

        this.repository.save(new SuperGroup(superGroupDTO));
    }

    public void removeGroup(SuperGroupId id) throws SuperGroupNotFoundException, SuperGroupHasGroupsException {
        if(this.groupFinder.getGroupsBySuperGroup(id).size() > 0) {
            throw new SuperGroupHasGroupsException();
        }

        this.repository.deleteById(id);
    }

    public void updateSuperGroup(SuperGroupDTO newSuperGroup) throws SuperGroupNotFoundException, IDsNotMatchingException {
        SuperGroup superGroup = this.finder.getSuperGroupEntity(newSuperGroup);
        superGroup.apply(newSuperGroup);
        this.repository.save(superGroup);
    }
}
