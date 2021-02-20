package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.data.*;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
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

    public boolean groupExistsByName(String name) {
        return this.groupRepository.existsByName(name);
    }

    public boolean groupExists(GroupId groupId) {
        return this.groupRepository.existsById(groupId);
    }

    public List<GroupDTO> getGroups() {
        return this.groupRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinified() {
        return this.getGroups().stream().map(GroupMinifiedDTO::new).collect(Collectors.toList());
    }

    public GroupDTO getGroup(GroupId id) throws GroupNotFoundException {
        return toDTO(getGroupEntity(id));
    }

    public GroupDTO getGroupByName(String name) throws GroupNotFoundException {
        return toDTO(getGroupEntityByName(name));
    }

    public GroupMinifiedDTO getGroupMinified(GroupId id) throws GroupNotFoundException {
        return new GroupMinifiedDTO(this.getGroup(id));
    }

    protected Group getGroupEntity(GroupId id) throws GroupNotFoundException {
        return this.groupRepository.findById(id)
                .orElseThrow(GroupNotFoundException::new);
    }

    protected Group getGroupEntity(GroupShallowDTO group) throws GroupNotFoundException {
        return getGroupEntity(group.getId());
    }

    protected Group getGroupEntity(GroupDTO group) throws GroupNotFoundException {
        return getGroupEntity(group.getId());
    }

    protected Group getGroupEntityByName(String name) throws GroupNotFoundException {
        return this.groupRepository.findByName(name)
                .orElseThrow(GroupNotFoundException::new);
    }

    public List<GroupDTO> getGroupsBySuperGroup(SuperGroupId superGroupId) throws SuperGroupNotFoundException {
        if(superGroupFinder.superGroupExists(superGroupId)) {
            throw new SuperGroupNotFoundException();
        }

        return this.groupRepository.findAllBySuperGroupId(superGroupId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinifiedBySuperGroup(SuperGroupId superGroupId) throws SuperGroupNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getActiveGroupsMinifiedBySuperGroup(SuperGroupId superGroupId) throws SuperGroupNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .filter(GroupBaseDTO::isActive)
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    protected GroupDTO toDTO(Group group) {
        try {
            return new GroupDTO.GroupDTOBuilder()
                    .id(group.getId())
                    .becomesActive(group.getBecomesActive())
                    .becomesInactive(group.getBecomesInactive())
                    .email(new Email(group.getEmail()))
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
                    .email(group.getEmail())
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
