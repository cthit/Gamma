package it.chalmers.gamma.group.service;

import it.chalmers.gamma.group.data.Group;
import it.chalmers.gamma.group.dto.*;
import it.chalmers.gamma.group.data.GroupRepository;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;
import it.chalmers.gamma.membership.service.MembershipFinder;
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
    private final MembershipFinder membershipFinder;

    public GroupFinder(GroupRepository groupRepository,
                       SuperGroupFinder superGroupFinder,
                       MembershipFinder membershipFinder) {
        this.groupRepository = groupRepository;
        this.superGroupFinder = superGroupFinder;
        this.membershipFinder = membershipFinder;
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

    public List<GroupDTO> getGroupsBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        if(superGroupFinder.superGroupExists(superGroupId)) {
            throw new SuperGroupNotFoundException();
        }

        return this.groupRepository.findAllBySuperGroupId(superGroupId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getGroupsMinifiedBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    public List<GroupMinifiedDTO> getActiveGroupsMinifiedBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .filter(GroupBaseDTO::isActive)
                .map(GroupMinifiedDTO::new)
                .collect(Collectors.toList());
    }

    public List<GroupWithMembersDTO> getActiveGroupsWithMembersBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return getGroupsWithMembersBySuperGroup(superGroupId)
                .stream()
                .filter(groupWithMembers -> groupWithMembers.getGroup().isActive())
                .collect(Collectors.toList());
    }

    public List<GroupWithMembersDTO> getGroupsWithMembersBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(this::withMembers)
                .collect(Collectors.toList());
    }

    public GroupWithMembersDTO withMembers(GroupDTO group) {
        return new GroupWithMembersDTO(group, membershipFinder.getRestrictedMembershipsInGroup(group));
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
