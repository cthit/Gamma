package it.chalmers.gamma.membership;

import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.group.FKITGroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.ITUserDTO;

import it.chalmers.gamma.membership.response.MembershipDoesNotExistResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import it.chalmers.gamma.user.ITUserFinder;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final GroupService groupService;
    private final PostService postService;
    private final NoAccountMembershipRepository noAccountMembershipRepository;
    private final ITUserFinder userFinder;

    public MembershipService(MembershipRepository membershipRepository,
                             GroupService groupService,
                             PostService postService,
                             NoAccountMembershipRepository noAccountMembershipRepository,
                             ITUserFinder userFinder) {
        this.membershipRepository = membershipRepository;
        this.groupService = groupService;
        this.postService = postService;
        this.noAccountMembershipRepository = noAccountMembershipRepository;
        this.userFinder = userFinder;
    }

    //TODO check which methods should be left in this class,
    // many are probably never going to be used.

    /**
     * adds a user to the group.
     *
     * @param group group the user should be added to
     * @param user  which user is added
     * @param post  what post the user has in group
     * @param postname what the unoficial-post name is
     */
    public MembershipDTO addUserToGroup(FKITGroupDTO group, ITUserDTO user, PostDTO post, String postname) {
        MembershipPK pk = new MembershipPK(
                user.getId(),
                group.getId(),
                post.getId()
        );
        Membership membership = new Membership();
        membership.setId(pk);
        membership.setUnofficialPostName(postname);
        return this.membershipRepository.save(membership).toDTO();
    }

    public List<MembershipDTO> getMembershipsInGroup(FKITGroupDTO group) {
        return this.membershipRepository
                .findAllById_GroupId(group.getId())
                .stream()
                .map(Membership::toDTO)
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
    public List<FKITGroupDTO> getUsersGroupDTO(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_UserId(
                user.getId()
        );

        return memberships.stream()
                .map(m -> groupService.getGroup(m.getId().getGroupId()))
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getUserDTOByGroupAndPost(FKITGroupDTO group, PostDTO post) {
        return this.membershipRepository
                .findAllById_GroupIdAndId_PostId(group.getId(), post.getId())
                .stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public List<FKITGroupDTO> getGroupsWithPost(PostDTO post) {
        List<Membership> memberships = this.membershipRepository.findAllById_PostId(post.getId());
        List<UUID> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            if (!groups.contains(membership.getId().getGroupId())) {
                groups.add(membership.getId().getGroupId());
            }
        }

        return groups
                .stream()
                .map(groupService::getGroup)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByUser(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(user.getId());
        return memberships.stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO user, FKITGroupDTO group) {
        return this.membershipRepository
                .findById_UserIdAndId_GroupId(
                        user.getId(),
                        group.getId()
                ).orElseThrow(MembershipDoesNotExistResponse::new)
                .toDTO();
    }

    public void removeUserFromGroup(FKITGroupDTO group, ITUserDTO user) {
        MembershipDTO membershipDTO = getMembershipByUserAndGroup(user, group);
        this.membershipRepository.delete(this.getMembership(membershipDTO));
    }

    @Transactional
    public void editMembership(MembershipDTO membershipDTO, String unofficialName, PostDTO post) {
        this.removeUserFromGroup(membershipDTO.getFkitGroupDTO(), membershipDTO.getUser());
        this.addUserToGroup(membershipDTO.getFkitGroupDTO(),
                membershipDTO.getUser(), post,
                unofficialName);
        this.membershipRepository.save(this.getMembership(membershipDTO));
    }

    public void removeAllUsersFromGroup(FKITGroupDTO group) {
        List<ITUserDTO> users = this.getMembershipsInGroup(group).stream()
                .map(MembershipDTO::getUser).collect(Collectors.toList());
        for (ITUserDTO user : users) {
            this.membershipRepository.delete(
                    this.getMembership(this.getMembershipByUserAndGroup(user, group)));
        }
    }

    public void removeAllMemberships(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(user.getId());
        memberships.forEach(this.membershipRepository::delete);
    }

    /**
     * This is a Legacy function, only to keep track of which users have been in different committees long ago.
     * @param fkitGroupDTO the group which to fetch users with no account for.
     * @return A list of NoAccountMemberships corresponding to members of that group.
     */
    public List<NoAccountMembershipDTO> getNoAccountMembership(FKITGroupDTO fkitGroupDTO) {
        Group group = this.groupService.fromDTO(fkitGroupDTO);
        return this.noAccountMembershipRepository.findAllById_Group(group).stream()
                .map(NoAccountMembership::toDTO).collect(Collectors.toList());
    }

    public boolean groupIsActiveCommittee(FKITGroupDTO group) {
        return group.isActive();
    }

    private Membership getMembership(MembershipDTO membershipDTO) {
        return this.membershipRepository.findById_UserIdAndId_GroupId(
                membershipDTO.getUser().getId(),
                membershipDTO.getFkitGroupDTO().getId()
        ).orElse(null);
    }

    public boolean isPostUsed(UUID id) {
        return !this.membershipRepository.findAllById_PostId(id).isEmpty();
    }
}
