package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.group.data.GroupBaseDTO;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.data.*;
import it.chalmers.gamma.domain.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.domain.user.data.UserRestrictedDTO;
import it.chalmers.gamma.domain.membership.data.UserRestrictedWithGroupsDTO;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Component
public class MembershipFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipFinder.class);

    private final MembershipRepository membershipRepository;
    private final GroupFinder groupFinder;
    private final UserFinder userFinder;
    private final PostFinder postFinder;

    public MembershipFinder(MembershipRepository membershipRepository,
                            GroupFinder groupFinder,
                            UserFinder userFinder,
                            PostFinder postFinder) {
        this.membershipRepository = membershipRepository;
        this.groupFinder = groupFinder;
        this.userFinder = userFinder;
        this.postFinder = postFinder;
    }

    public List<MembershipDTO> getMembershipsInGroup(UUID groupId) {
        return this.membershipRepository
                .findAllById_GroupId(groupId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MembershipRestrictedDTO> getRestrictedMembershipsInGroup(UUID groupId) {
        return getMembershipsInGroup(groupId)
                .stream()
                .map(MembershipRestrictedDTO::new)
                .collect(Collectors.toList());
    }

    public List<MembershipRestrictedDTO> getRestrictedMembershipsInGroup(GroupDTO group) {
        return getRestrictedMembershipsInGroup(group.getId());
    }

    public List<MembershipRestrictedDTO> getUserByGroupAndPost(UUID groupId, UUID postId) {
        return this.membershipRepository
                .findAllById_GroupIdAndId_PostId(groupId, postId)
                .stream().map(this::toRestrictedDTO).collect(Collectors.toList());
    }

    public List<GroupDTO> getGroupsWithPost(UUID postId) {
        List<Membership> memberships = this.membershipRepository.findAllById_PostId(postId);
        List<UUID> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            if (!groups.contains(membership.getId().getGroupId())) {
                groups.add(membership.getId().getGroupId());
            }
        }

        return groups
                .stream()
                .map(this::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private GroupDTO getGroup(UUID groupId) {
        try{
            return this.groupFinder.getGroup(groupId);
        } catch(GroupNotFoundException e) {
            LOGGER.error("Group with id " + groupId + " not found", e);
        }

        return null;
    }

    public List<MembershipDTO> getMembershipsByUser(UserId userId) throws UserNotFoundException {
        if(!this.userFinder.userExists(userId)) {
            throw new UserNotFoundException();
        }

        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(userId);
        return memberships.stream().map(this::toDTO).collect(Collectors.toList());
    }

    protected Membership getMembershipEntityByUserGroupPost(UserId userId, UUID groupId, UUID postId) throws MembershipNotFoundException {
        return this.membershipRepository.findById(new MembershipPK(postId, groupId, userId))
                .orElseThrow(MembershipNotFoundException::new);
    }


    public List<MembershipsPerGroupDTO> getActiveGroupsWithMemberships() {
        return this.groupFinder.getGroups()
                .stream()
                .filter(GroupBaseDTO::isActive)
                .map(this::withMembers)
                .collect(Collectors.toList());
    }

    public List<MembershipsPerGroupDTO> getActiveGroupsWithMembershipsBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return this.getGroupsWithMembershipsBySuperGroup(superGroupId)
                .stream()
                .filter(groupWithMembers -> groupWithMembers.getGroup().isActive())
                .collect(Collectors.toList());
    }

    public List<MembershipsPerGroupDTO> getGroupsWithMembershipsBySuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
        return this.groupFinder.getGroupsBySuperGroup(superGroupId)
                .stream()
                .map(this::withMembers)
                .collect(Collectors.toList());
    }

    public MembershipsPerGroupDTO withMembers(GroupDTO group) {
        return new MembershipsPerGroupDTO(group, this.getRestrictedMembershipsInGroup(group));
    }


    public List<UserRestrictedWithGroupsDTO> getUsersWithMembership() {
        return this.userFinder.getUsers()
                .stream()
                .map(user -> {
                    try {
                        return this.toWithGroupsRestricted(new UserRestrictedDTO(user));
                    } catch (UserNotFoundException ignored) {
                        //ignored since we're going directly from every user
                        return new UserRestrictedWithGroupsDTO(new UserRestrictedDTO(user), new ArrayList<>());
                    }
                })
                .collect(Collectors.toList());
    }

    public UserRestrictedWithGroupsDTO getUserRestrictedWithMemberships(Cid cid) throws UserNotFoundException {
        return this.toWithGroupsRestricted(new UserRestrictedDTO(this.userFinder.getUser(cid)));
    }

    public UserRestrictedWithGroupsDTO getUserRestrictedWithMemberships(UserId id) throws UserNotFoundException {
        return this.toWithGroupsRestricted(new UserRestrictedDTO(this.userFinder.getUser(id)));
    }

    public UserWithGroupsDTO getUserWithMemberships(UserId id) throws UserNotFoundException {
        return this.toWithGroups(this.userFinder.getUser(id));
    }

    private UserWithGroupsDTO toWithGroups(UserDTO user) throws UserNotFoundException {
        return new UserWithGroupsDTO(user,
                this.getMembershipsByUser(user.getId())
                        .stream()
                        .map(membership -> new UserWithGroupsDTO.UserGroup(membership.getPost(), membership.getGroup()))
                        .collect(Collectors.toList()));
    }

    private UserRestrictedWithGroupsDTO toWithGroupsRestricted(UserRestrictedDTO user) throws UserNotFoundException {
        return new UserRestrictedWithGroupsDTO(user,
                this.getMembershipsByUser(user.getId())
                        .stream()
                        .map(membership -> new UserRestrictedWithGroupsDTO.UserGroup(membership.getPost(), membership.getGroup()))
                        .collect(Collectors.toList()));
    }

    private MembershipDTO toDTO(Membership membership) {
        try {
            return new MembershipDTO.MembershipDTOBuilder()
                    .group(this.groupFinder.getGroup(membership.getId().getGroupId()))
                    .user(this.userFinder.getUser(membership.getId().getUserId()))
                    .post(this.postFinder.getPost(membership.getId().getPostId()))
                    .unofficialPostName(membership.getUnofficialPostName())
                    .build();
        } catch (GroupNotFoundException | UserNotFoundException | PostNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<MembershipsPerGroupDTO> getPostUsages(UUID postId) {
        List<GroupDTO> groups = this.getGroupsWithPost(postId);
        return groups.stream()
                .map(group -> new MembershipsPerGroupDTO(group, this.getUserByGroupAndPost(group.getId(), postId)))
                .collect(Collectors.toList());
    }

    private MembershipRestrictedDTO toRestrictedDTO(Membership membership) {
        return new MembershipRestrictedDTO(Objects.requireNonNull(toDTO(membership)));
    }

}
