package it.chalmers.gamma.group;

import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.SuperGroupService;

import java.util.Calendar;
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

    public void createGroup(GroupDTO group) {
        this.repo.save(new Group(group));
    }

    public GroupDTO editGroup(UUID id, GroupDTO groupDTO) {
        Group group = this.groupFinder.getGroup(id);
        if (group == null) {
            return null;
        }
        group.getFunction().setSv(groupDTO.getFunction() == null
                ? group.getFunction().getSv() : groupDTO.getFunction().getSv());

        group.getFunction().setEn(groupDTO.getFunction() == null
                ? group.getFunction().getEn() : groupDTO.getFunction().getEn());

        if (groupDTO.getDescription() != null && group.getDescription() != null) {
            group.getDescription().setSv(groupDTO.getDescription().getSv());
            group.getDescription().setEn(groupDTO.getDescription().getEn());
        }
        return saveGroup(group, groupDTO.getPrettyName(), groupDTO.getBecomesActive(),
                groupDTO.getBecomesInactive(),
                groupDTO.getEmail(), groupDTO.getAvatarURL(), groupDTO.getSuperGroup());
    }

    public void removeGroup(String name) {
        this.repo.deleteByName(name);
    }

    public void removeGroup(UUID groupId) {
        this.repo.deleteById(groupId);
    }

    public void editGroupAvatar(GroupDTO groupDTO, String url) {
        Group group = this.fromDTO(groupDTO);
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        group.setAvatarURL(url);
        this.repo.save(group);
    }

}
