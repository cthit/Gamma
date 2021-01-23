package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.GroupDTO;
import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.group.GroupFinder;
import it.chalmers.gamma.membership.response.MembershipDoesNotExistResponse;
import it.chalmers.gamma.noaccountmembership.NoAccountMembership;
import it.chalmers.gamma.noaccountmembership.NoAccountMembershipDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MembershipFinder {

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
     * @param user which user which group membersships should be queried
     * @return The UUIDs of the groups the user is a part of
     */
    public List<GroupDTO> getUsersGroupDTO(UserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_UserId(
                user.getId()
        );

        return memberships.stream()
                .map(m -> this.groupFinder.getGroup(m.getId().getGroupId()))
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
                .map(this.groupFinder::getGroup)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByUser(UserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(user.getId());
        return memberships.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(UserDTO user, GroupDTO group) {
        return this.membershipRepository
                .findById_UserIdAndId_GroupId(
                        user.getId(),
                        group.getId()
                ).map(this::toDTO)
                .orElseThrow(MembershipDoesNotExistResponse::new);
    }

    private Membership toEntity(MembershipDTO membershipDTO) {
        return this.membershipRepository.findById_UserIdAndId_GroupId(
                membershipDTO.getUser().getId(),
                membershipDTO.getFkitGroupDTO().getId()
        ).orElse(null);
    }

    private MembershipDTO toDTO(Membership membership) {
        return null;
    }

}
