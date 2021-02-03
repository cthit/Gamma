package it.chalmers.gamma.group.service;

import it.chalmers.gamma.group.data.Group;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.data.GroupRepository;
import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import it.chalmers.gamma.group.dto.GroupShallowDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupFinder;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
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

    public boolean groupExists(UUID groupId) {
        return this.groupRepository.existsById(groupId);
    }

    public List<GroupDTO> getGroups() {
        return this.groupRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinified() {
        return this.getGroups().stream().map(GroupMinifiedDTO::new).collect(Collectors.toList());
    }

    public GroupDTO getGroup(UUID id) throws GroupNotFoundException {
        return toDTO(getGroupEntity(id));
    }

    public GroupMinifiedDTO getGroupMinified(UUID id) throws GroupNotFoundException {
        return new GroupMinifiedDTO(this.getGroup(id));
    }

    protected Group getGroupEntity(UUID id) throws GroupNotFoundException {
        return this.groupRepository.findById(id)
                .orElseThrow(GroupNotFoundException::new);
    }

    public Group getGroupEntity(GroupShallowDTO group) throws GroupNotFoundException {
        return getGroupEntity(group.getId());
    }

    public Group getGroupEntity(GroupDTO group) throws GroupNotFoundException {
        return getGroupEntity(group.getId());
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
        try {
            return new GroupDTO.GroupDTOBuilder()
                    .id(group.getId())
                    .becomesActive(group.getBecomesActive())
                    .becomesInactive(group.getBecomesInactive())
                    .description(group.getDescription())
                    .email(group.getEmail())
                    .function(group.getFunction())
                    .name(group.getName())
                    .prettyName(group.getPrettyName())
                    .avatarUrl(group.getAvatarURL())
                    .superGroup(superGroupFinder.getSuperGroup(group.getSuperGroupId()))
                    .build();
        } catch (SuperGroupNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected GroupDTO fromShallow(GroupShallowDTO group) {
        try {
            return new GroupDTO.GroupDTOBuilder()
                    .id(group.getId())
                    .becomesActive(group.getBecomesActive())
                    .becomesInactive(group.getBecomesInactive())
                    .description(group.getDescription())
                    .email(group.getEmail())
                    .function(group.getFunction())
                    .name(group.getName())
                    .prettyName(group.getPrettyName())
                    .avatarUrl(group.getAvatarURL())
                    .superGroup(superGroupFinder.getSuperGroup(group.getSuperGroupId()))
                    .build();
        } catch (SuperGroupNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
