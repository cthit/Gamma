package it.chalmers.gamma.membership.service;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.service.GroupFinder;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.membership.data.Membership;
import it.chalmers.gamma.membership.data.MembershipRepository;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;
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

    public MembershipFinder(MembershipRepository membershipRepository,
                            GroupFinder groupFinder) {
        this.membershipRepository = membershipRepository;
        this.groupFinder = groupFinder;
    }

    public List<MembershipDTO> getMembershipsInGroup(GroupDTO group) {
        return this.membershipRepository
                .findAllById_GroupId(group.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsByPost(PostDTO post) {
        return this.membershipRepository.findAllById_PostId(post.getId());
    }


    /**
     * gets which groups a user is a part of.
     *
     * @param user which user which group memberships should be queried
     * @return The UUIDs of the groups the user is a part of
     */
    public List<GroupDTO> getUsersGroupDTO(UserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_UserId(
                user.getId()
        );

        return memberships.stream()
                .map(m -> this.getGroup(m.getId().getGroupId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getUserDTOByGroupAndPost(GroupDTO group, PostDTO post) {
        return this.membershipRepository
                .findAllById_GroupIdAndId_PostId(group.getId(), post.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<GroupDTO> getGroupsWithPost(PostDTO post) {
        List<Membership> memberships = this.membershipRepository.findAllById_PostId(post.getId());
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

    public List<MembershipDTO> getMembershipsByUser(UserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(user.getId());
        return memberships.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(UserDTO user, GroupDTO group) throws MembershipNotFoundException {
        return toDTO(this.getMembershipEntityByUserAndGroup(user.getId(), group.getId()));
    }

    protected Membership getMembershipEntityByUserAndGroup(UUID userId, UUID groupId) throws MembershipNotFoundException {
        return this.membershipRepository.findById_UserIdAndId_GroupId(userId, groupId)
                .orElseThrow(MembershipNotFoundException::new);
    }

    private MembershipDTO toDTO(Membership membership) {
        return new MembershipDTO(

        );
    }

}
