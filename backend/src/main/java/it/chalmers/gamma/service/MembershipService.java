package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final FKITGroupService fkitGroupService;
    private final DTOToEntityService dtoToEntityService;
    private final PostService postService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;
    private final FKITSuperGroupService fkitSuperGroupService;


    public MembershipService(MembershipRepository membershipRepository, FKITGroupService fkitGroupService, DTOToEntityService dtoToEntityService, PostService postService, FKITGroupToSuperGroupService fkitGroupToSuperGroupService, FKITSuperGroupService fkitSuperGroupService) {
        this.membershipRepository = membershipRepository;
        this.fkitGroupService = fkitGroupService;
        this.dtoToEntityService = dtoToEntityService;
        this.postService = postService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.fkitSuperGroupService = fkitSuperGroupService;
    }


    //TODO check which methods should be left in this class,
    // many are probably never going to be used.

    //TODO this should perhaps be rewritten to return actual objects instead of the GROUP_ID:s of objects.


    /**
     * adds a userDTO to the groupDTO.
     *
     * @param groupDTO groupDTO the userDTO should be added to
     * @param userDTO  which userDTO is added
     * @param postDTO  what post the userDTO has in groupDTO
     * @param postname what the unoficial-post name is
     */
    public void addUserToGroup(FKITGroupDTO groupDTO, ITUserDTO userDTO, PostDTO postDTO, String postname) {
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroup(this.fkitGroupService.getGroup(groupDTO));
        pk.setITUser(this.dtoToEntityService.fromDTO(userDTO));
        pk.setPost(this.postService.getPost(postDTO));
        Membership membership = new Membership();
        membership.setId(pk);
        membership.setUnofficialPostName(postname);
        this.membershipRepository.save(membership);
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
                .findAllById_FkitGroup(this.fkitGroupService.getGroup(group))
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
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(this.dtoToEntityService.fromDTO(user));
        List<FKITGroupDTO> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            FKITGroupDTO group = membership.getId().getFKITGroup().toDTO();
            membership.setFkitSuperGroups(this.fkitGroupToSuperGroupService.getSuperGroups(group)
                    .stream().map(this.fkitSuperGroupService::getGroup).collect(Collectors.toList()));
            groups.add(membership.getId().getFKITGroup().toDTO());
        }
        return groups;
    }

    /**
     * finds which group the userDTO has a specific postDTO in.
     */
    public FKITGroupDTO getGroupDTOIdByUserAndPost(ITUserDTO userDTO, PostDTO postDTO) {
        Membership membership = this.membershipRepository
                .findById_ItUserAndId_Post(
                        this.dtoToEntityService.fromDTO(userDTO),
                        this.postService.getPost(postDTO));
        return membership.getId().getFKITGroup().toDTO();
    }

    public List<MembershipDTO> getUserDTOByGroupAndPost(FKITGroupDTO group, PostDTO post) {
        return this.membershipRepository.findAllById_FkitGroupAndId_Post(
                this.fkitGroupService.getGroup(group),
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
        for (Membership membership : memberships) {
            FKITGroupDTO group = membership.getId().getFKITGroup().toDTO();
            membership.setFkitSuperGroups(this.fkitGroupToSuperGroupService.getSuperGroups(group)
                    .stream().map(this.fkitSuperGroupService::getGroup).collect(Collectors.toList()));
        }
        return memberships.stream().map(Membership::toDTO).collect(Collectors.toList());
    }
    public List<GroupType> getGroupType(FKITGroupDTO groupDTO) {
        return this.fkitGroupToSuperGroupService.getSuperGroups(groupDTO).stream().map(FKITSuperGroupDTO::getType)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsByUser(ITUser user) {
        return this.membershipRepository.findAllById_ItUser(user);
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO userDTO, FKITGroupDTO groupDTO) {
        return this.membershipRepository
                .findById_ItUserAndId_FkitGroup(
                        this.dtoToEntityService.fromDTO(userDTO),
                        this.fkitGroupService.getGroup(groupDTO)).toDTO();
    }

    public void removeUserFromGroup(FKITGroupDTO group, ITUserDTO user) {
        MembershipDTO membershipDTO = getMembershipByUserAndGroup(user, group);
        this.membershipRepository.delete(this.getMembership(membershipDTO));
    }

    public void editMembership(MembershipDTO membershipDTO, String unofficialName, PostDTO post) {
        Membership membership = this.getMembership(membershipDTO);
        membership.setUnofficialPostName(unofficialName);
        membership.getId().setPost(this.postService.getPost(post));
        this.membershipRepository.save(membership);
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

    public boolean groupIsActiveCommittee(FKITGroupDTO group) {
        return this.getGroupType(group).stream()
                .anyMatch(type -> type.equals(GroupType.COMMITTEE)) && group.isActive();
    }

    public List<Membership> getMembershipsFilterByPostAndGroupType(PostDTO postDTO, GroupType groupType) {
        Post post = this.postService.getPost(postDTO);
        List<FKITGroupDTO> groups = this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(groupType))
                .map(this.fkitGroupToSuperGroupService::getActiveGroups)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return groups.stream().map(g -> this.membershipRepository
                .findAllById_FkitGroupAndId_Post(this.fkitGroupService.getGroup(g), post))
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    private Membership getMembership(MembershipDTO membershipDTO) {
        return this.membershipRepository.findById(membershipDTO.getFkitGroupDTO().getId()).orElse(null);
    }
}
