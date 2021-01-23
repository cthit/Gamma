package it.chalmers.gamma.group;

import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupFinder;
import it.chalmers.gamma.util.UUIDUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GroupFinder {

    private final GroupRepository groupRepository;
    private final SuperGroupFinder superGroupFinder;

    public GroupFinder(GroupRepository groupRepository,
                       SuperGroupFinder superGroupFinder) {
        this.groupRepository = groupRepository;
        this.superGroupFinder = superGroupFinder;
    }


    public List<GroupDTO> getGroups() {
        return this.groupRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public GroupDTO getGroup(UUID id) {
        return this.groupRepository.findById(id).map(this::toDTO).orElseThrow(GroupDoesNotExistResponse::new);
    }

    public GroupDTO getGroup(String name) {
        if (UUIDUtil.validUUID(name)) {
            return getGroup(UUID.fromString(name));
        }
        return this.groupRepository.findByName(name.toLowerCase())
                .map(this::toDTO)
                .orElseThrow(GroupDoesNotExistResponse::new);
    }


    public List<GroupDTO> getAllGroupsWithSuperGroup(SuperGroupDTO superGroupDTO) {
        return this.groupRepository.findAllBySuperGroupId(superGroupDTO.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getAllActiveGroups() {
        return this.getGroups().stream().filter(GroupDTO::isActive).collect(Collectors.toList());
    }

    public List<GroupDTO> getActiveGroups(SuperGroupDTO superGroup) {
        return this.getAllGroupsWithSuperGroup(superGroup).stream()
                .filter(GroupDTO::isActive)
                .collect(Collectors.toList());
    }

    protected GroupDTO toDTO(Group group) {
        return new GroupDTO.GroupDTOBuilder().id(group.getId()).becomesActive(group.getBecomesActive()).becomesInactive(group.getBecomesInactive()).description(group.getDescription()).email(group.getEmail()).function(group.getFunction()).name(group.getName()).prettyName(group.getPrettyName()).avatarUrl(group.getAvatarURL()).superGroup(superGroupFinder.getSuperGroup(group.getSuperGroupId()).orElseThrow()).createGroupDTO();
    }

}
