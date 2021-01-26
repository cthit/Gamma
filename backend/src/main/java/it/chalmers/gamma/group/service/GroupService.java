package it.chalmers.gamma.group.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.group.data.Group;
import it.chalmers.gamma.group.data.GroupRepository;
import it.chalmers.gamma.group.dto.GroupShallowDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.SuperGroupService;

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

    public void createGroup(GroupShallowDTO group) {
        this.repo.save(new Group(groupFinder.fromShallow(group)));
    }

    public void editGroup(GroupShallowDTO oldGroup) throws GroupNotFoundException, IDsNotMatchingException {
        Group group = this.groupFinder.getGroupEntity(oldGroup.getId());
        group.apply(this.groupFinder.fromShallow(oldGroup));
        this.repo.save(group);
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public void editGroupAvatar(UUID groupId, String url) throws GroupNotFoundException {
        Group group = groupFinder.getGroupEntity(groupId);
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        group.setAvatarURL(url);
        this.repo.save(group);
    }

}
