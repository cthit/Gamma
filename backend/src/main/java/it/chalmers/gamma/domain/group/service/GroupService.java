package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.data.Group;
import it.chalmers.gamma.domain.group.data.GroupRepository;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.data.GroupShallowDTO;
import it.chalmers.gamma.domain.group.exception.GroupAlreadyExistsException;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupService;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class GroupService {

    private final GroupRepository repo;
    private final SuperGroupService superGroupService;
    private final GroupFinder groupFinder;

    public GroupService(GroupRepository repo,
                        SuperGroupService superGroupService,
                        GroupFinder groupFinder) {
        this.repo = repo;
        this.superGroupService = superGroupService;
        this.groupFinder = groupFinder;
    }

    public void createGroup(GroupShallowDTO group) throws GroupAlreadyExistsException {
        this.createGroup(groupFinder.fromShallow(group));
    }

    public void createGroup(GroupDTO group) throws GroupAlreadyExistsException {
        if(this.groupFinder.groupExists(group.getId()) || this.groupFinder.groupExistsByName(group.getName())) {
            throw new GroupAlreadyExistsException();
        }
        this.repo.save(new Group(group));
    }

    public void editGroup(GroupDTO newEdit) throws GroupNotFoundException, IDsNotMatchingException {
        Group group = this.groupFinder.getGroupEntity(newEdit);
        group.apply(newEdit);
        this.repo.save(group);
    }

    public void editGroup(GroupShallowDTO newEdit) throws GroupNotFoundException, IDsNotMatchingException {
        Group group = this.groupFinder.getGroupEntity(newEdit);
        group.apply(groupFinder.fromShallow(newEdit));
        this.repo.save(group);
    }

    public void removeGroup(UUID groupId) throws GroupNotFoundException {
        if(!this.groupFinder.groupExists(groupId)) {
            throw new GroupNotFoundException();
        }

        this.repo.deleteById(groupId);
    }

}
