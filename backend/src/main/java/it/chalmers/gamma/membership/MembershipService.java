package it.chalmers.gamma.membership;

import it.chalmers.gamma.db.entity.NoAccountMembership;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.group.Group;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.post.PostService;
import it.chalmers.gamma.domain.group.FKITGroupDTO;
import it.chalmers.gamma.domain.membership.MembershipDTO;
import it.chalmers.gamma.domain.membership.NoAccountMembershipDTO;
import it.chalmers.gamma.domain.post.PostDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;

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
     * adds a userDTO to the groupDTO.
     *
     * @param groupDTO groupDTO the userDTO should be added to
     * @param userDTO  which userDTO is added
     * @param postDTO  what post the userDTO has in groupDTO
     * @param postname what the unoficial-post name is
     */
    public MembershipDTO addUserToGroup(FKITGroupDTO groupDTO, ITUserDTO userDTO, PostDTO postDTO, String postname) {
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroup(this.groupService.fromDTO(groupDTO));
        pk.setITUser(this.userFinder.getUserEntity(userDTO));
        pk.setPost(this.postService.getPost(postDTO));
        Membership membership = new Membership();
        membership.setId(pk);
        membership.setUnofficialPostName(postname);
        return this.membershipRepository.save(membership).toDTO();
    }

    /**
     * finds all users that has a specific post.
     *
     * @param postDTO which post that should be looked for
     * @return the users UUID, the identifier for the user
     */
    public List<ITUserDTO> getPostHoldersDTO(PostDTO postDTO) {
        List<Membership> memberships = this.membershipRepository.findAllById_Post(this.postService.getPost(postDTO));
        List<ITUserDTO> usersId = new ArrayList<>();
        for (Membership membership : memberships) {
            usersId.add(membership.getId().getITUser().toDTO());
        }
        return usersId;
    }

    public List<MembershipDTO> getMembershipsInGroup(FKITGroupDTO group) {
        return this.membershipRepository
                .findAllById_Group(this.groupService.fromDTO(group))
                .stream()
                .map(Membership::toDTO)
                .collect(Collectors.toList());
    }
    public List<Membership> getMembershipsByPost(PostDTO post) {
        return this.membershipRepository.findAllById_Post(this.postService.getPost(post));
    }


    /**
     * gets which groups a user is a part of.
     *
     * @param user which user which group membersships should be queried
     * @return The UUIDs of the groups the user is a part of
     */
    public List<FKITGroupDTO> getUsersGroupDTO(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(
                this.userFinder.getUserEntity(user)
        );
        return memberships.stream()
                .map(m -> m.getId().getFKITGroup().toDTO()).collect(Collectors.toList());
    }

    public List<MembershipDTO> getUserDTOByGroupAndPost(FKITGroupDTO group, PostDTO post) {
        return this.membershipRepository.findAllById_GroupAndId_Post(
                this.groupService.fromDTO(group),
                this.postService.getPost(post)).stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public List<FKITGroupDTO> getGroupsWithPost(PostDTO postDTO) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_Post(this.postService.getPost(postDTO));
        List<FKITGroupDTO> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            if (!groups.contains(membership.getId().getFKITGroup().toDTO())) {
                groups.add(membership.getId().getFKITGroup().toDTO());
            }
        }
        return groups;
    }

    public List<MembershipDTO> getMembershipsByUser(ITUserDTO userDTO) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_ItUser(this.userFinder.getUserEntity(userDTO));
        return memberships.stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO userDTO, FKITGroupDTO groupDTO) {
        return this.membershipRepository
                .findById_ItUserAndId_Group(
                        this.userFinder.getUserEntity(userDTO),
                        this.groupService.fromDTO(groupDTO)).orElseThrow(MembershipDoesNotExistResponse::new)
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
                .findAllById_ItUser(this.userFinder.getUserEntity(user));
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
        return this.membershipRepository.findById_ItUserAndId_Group(
                this.userFinder.getUserEntity(membershipDTO.getUser()),
                this.groupService.fromDTO(membershipDTO.getFkitGroupDTO())).orElse(null);
    }

    public boolean isPostUsed(UUID id) {
        return !this.membershipRepository.findAllById_PostId(id).isEmpty();
    }
}
