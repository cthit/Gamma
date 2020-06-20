package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.NoAccountMembership;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import it.chalmers.gamma.db.repository.NoAccountMembershipRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.domain.dto.membership.NoAccountMembershipDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;

import it.chalmers.gamma.response.membership.MembershipDoesNotExistResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final FKITGroupService fkitGroupService;
    private final DTOToEntityService dtoToEntityService;
    private final PostService postService;
    private final NoAccountMembershipRepository noAccountMembershipRepository;

    public MembershipService(MembershipRepository membershipRepository,
                             FKITGroupService fkitGroupService,
                             DTOToEntityService dtoToEntityService,
                             PostService postService, NoAccountMembershipRepository noAccountMembershipRepository) {
        this.membershipRepository = membershipRepository;
        this.fkitGroupService = fkitGroupService;
        this.dtoToEntityService = dtoToEntityService;
        this.postService = postService;
        this.noAccountMembershipRepository = noAccountMembershipRepository;
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
        pk.setFKITGroup(this.fkitGroupService.fromDTO(groupDTO));
        pk.setITUser(this.dtoToEntityService.fromDTO(userDTO));
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
                .findAllById_FkitGroup(this.fkitGroupService.fromDTO(group))
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
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(this.dtoToEntityService
                .fromDTO(user));
        return memberships.stream()
                .map(m -> m.getId().getFKITGroup().toDTO()).collect(Collectors.toList());
    }

    /**
     * finds which group the userDTO has a specific postDTO in.
     */
    public FKITGroupDTO getGroupDTOIdByUserAndPost(ITUserDTO userDTO, PostDTO postDTO) {
        Membership membership = this.membershipRepository
                .findById_ItUserAndId_Post(
                        this.dtoToEntityService.fromDTO(userDTO),
                        this.postService.getPost(postDTO)).orElseThrow(MembershipDoesNotExistResponse::new);
        return membership.getId().getFKITGroup().toDTO();
    }

    public List<MembershipDTO> getUserDTOByGroupAndPost(FKITGroupDTO group, PostDTO post) {
        return this.membershipRepository.findAllById_FkitGroupAndId_Post(
                this.fkitGroupService.fromDTO(group),
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
                .findAllById_ItUser(this.dtoToEntityService.fromDTO(userDTO));
        return memberships.stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO userDTO, FKITGroupDTO groupDTO) {
        return this.membershipRepository
                .findById_ItUserAndId_FkitGroup(
                        this.dtoToEntityService.fromDTO(userDTO),
                        this.fkitGroupService.fromDTO(groupDTO)).orElseThrow(MembershipDoesNotExistResponse::new)
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
                .findAllById_ItUser(this.dtoToEntityService.fromDTO(user));
        memberships.forEach(this.membershipRepository::delete);
    }

    /**
     * This is a Legacy function, only to keep track of which users have been in different committees long ago.
     * @param fkitGroupDTO the group which to fetch users with no account for.
     * @return A list of NoAccountMemberships corresponding to members of that group.
     */
    public List<NoAccountMembershipDTO> getNoAccountMembership(FKITGroupDTO fkitGroupDTO) {
        FKITGroup group = this.fkitGroupService.fromDTO(fkitGroupDTO);
        return this.noAccountMembershipRepository.findAllById_FkitGroup(group).stream()
                .map(NoAccountMembership::toDTO).collect(Collectors.toList());
    }

    public boolean groupIsActiveCommittee(FKITGroupDTO group) {
        return group.isActive();
    }

    private Membership getMembership(MembershipDTO membershipDTO) {
        return this.membershipRepository.findById_ItUserAndId_FkitGroup(
                this.dtoToEntityService.fromDTO(membershipDTO.getUser()),
                this.fkitGroupService.fromDTO(membershipDTO.getFkitGroupDTO())).orElse(null);
    }

    public boolean isPostUsed(UUID id) {
        return !membershipRepository.findAllById_PostId(id).isEmpty();
    }
}
