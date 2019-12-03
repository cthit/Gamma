package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final FKITGroupService fkitGroupService;
    private final ITUserService itUserService;
    private final PostService postService;

    public MembershipService(MembershipRepository membershipRepository, FKITGroupService fkitGroupService, ITUserService itUserService, PostService postService) {
        this.membershipRepository = membershipRepository;
        this.fkitGroupService = fkitGroupService;
        this.itUserService = itUserService;
        this.postService = postService;
    }


    //TODO check which methods should be left in this class,
    // many are probably never going to be used.

    //TODO this should perhaps be rewritten to return actual objects instead of the GROUP_ID:s of objects.


    /**
     * adds a userDTO to the groupDTO.
     *
     * @param groupDTO    groupDTO the userDTO should be added to
     * @param userDTO     which userDTO is added
     * @param postDTO     what post the userDTO has in groupDTO
     * @param postname    what the unoficial-post name is
     */
    public void addUserToGroup(FKITGroupDTO groupDTO, ITUserDTO userDTO, PostDTO postDTO, String postname) {
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroup(this.fkitGroupService.getGroup(groupDTO));
        pk.setITUser(this.itUserService.getITUser(userDTO));
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

    public List<ITUserDTO> getUsersInGroupDTO(FKITGroupDTO group) {
        List<MembershipDTO> memberships = this.membershipRepository
                .findAllById_FkitGroup(this.fkitGroupService.getGroup(group))
            .stream()
            .map(Membership::toDTO)
            .collect(Collectors.toList());
        List<ITUserDTO> groupIds = new ArrayList<>();
        for (MembershipDTO membership : memberships) {
            groupIds.add(membership.getUser());
        }
        return groupIds;
    }

    /**
     * gets which groups a user is a part of.
     *
     * @param user which user which group membersships should be queried
     * @return The UUIDs of the groups the user is a part of
     */
    public List<FKITGroupDTO> getUsersGroupDTO(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(this.itUserService.getITUser(user));
        List<FKITGroupDTO> groups = new ArrayList<>();
        for (Membership membership : memberships) {
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
                        this.itUserService.getITUser(userDTO),
                        this.postService.getPost(postDTO));
        return membership.getId().getFKITGroup().toDTO();
    }

    public List<ITUserDTO> getUserDTOByGroupAndPost(FKITGroupDTO group, PostDTO post) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_FkitGroupAndId_Post(
                        this.fkitGroupService.getGroup(group),
                        this.postService.getPost(post));
        List<ITUserDTO> users = new ArrayList<>();
        for (Membership membership : memberships) {
            users.add(membership.getId().getITUser().toDTO());
        }
        return users;
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
        return this.membershipRepository.findAllById_ItUser(this.itUserService.getITUser(userDTO))
                .stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO userDTO, FKITGroupDTO groupDTO) {
        return this.membershipRepository
                .findById_ItUserAndId_FkitGroup(
                        this.itUserService.getITUser(userDTO),
                        this.fkitGroupService.getGroup(groupDTO)).toDTO();
    }

    public void removeUserFromGroup(FKITGroupDTO group, ITUserDTO user) {
        MembershipDTO membershipDTO = getMembershipByUserAndGroup(user, group);
        this.membershipRepository.delete(this.getMembership(membershipDTO));
    }

    public void editMembership(MembershipDTO membershipDTO, String unofficialName, Post post) {
        Membership membership = this.getMembership(membershipDTO);
        membership.setUnofficialPostName(unofficialName);
        membership.getId().setPost(post);
        this.membershipRepository.save(membership);
    }

    public void removeAllUsersFromGroup(FKITGroupDTO group) {
        List<ITUser> users = this.getUsersInGroupDTO(group).stream()
                .map(this.itUserService::getITUser).collect(Collectors.toList());
        for (ITUser user : users) {
            this.membershipRepository.delete(
                    this.getMembership(this.getMembershipByUserAndGroup(user.toDTO(), group)));
        }
    }

    public void removeAllMemberships(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository
                .findAllById_ItUser(this.itUserService.getITUser(user));
        memberships.forEach(this.membershipRepository::delete);
    }

    protected Membership getMembership(MembershipDTO membershipDTO) {
        return this.membershipRepository.findById(membershipDTO.getFkitGroupDTO().getId()).orElse(null);
    }
}
