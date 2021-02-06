package it.chalmers.gamma.supergroup;

import java.util.UUID;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.group.service.GroupFinder;
import it.chalmers.gamma.supergroup.exception.SuperGroupHasGroupsException;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
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

    public SuperGroupDTO createSuperGroup(SuperGroupDTO superGroupDTO) {
        SuperGroup group = new SuperGroup();
        UUID id = superGroupDTO.getId();
        if (id == null) {
            id = UUID.randomUUID();
        }
        group.setId(id);
        group.setName(superGroupDTO.getName());
        group.setPrettyName(superGroupDTO.getPrettyName() == null
                ? superGroupDTO.getName() : superGroupDTO.getPrettyName());
        group.setType(superGroupDTO.getType());
        group.setEmail(superGroupDTO.getEmail());
        return this.repository.save(group).toDTO();
    }


    public void removeGroup(UUID id) throws SuperGroupNotFoundException, SuperGroupHasGroupsException {
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
