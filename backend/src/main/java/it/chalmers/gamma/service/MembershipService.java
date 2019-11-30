package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }


    //TODO check which methods should be left in this class,
    // many are probably never going to be used.

    //TODO this should perhaps be rewritten to return actual objects instead of the GROUP_ID:s of objects.


    /**
     * adds a user to the group.
     *
     * @param group    group the user should be added to
     * @param user     which user is added
     * @param post     what post the user has in group
     * @param postname what the unoficial-post name is
     */
    public void addUserToGroup(FKITGroupDTO group, ITUserDTO user, Post post, String postname) {
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroup(group);
        pk.setITUser(user);
        pk.setPost(post);
        Membership membership = new Membership();
        membership.setId(pk);
        membership.setUnofficialPostName(postname);
        this.membershipRepository.save(membership);
    }

    /**
     * finds all users that has a specific post.
     *
     * @param post which post that should be looked for
     * @return the users UUID, the identifier for the user
     */
    public List<ITUser> getPostHoldersIds(Post post) {
        List<Membership> memberships = this.membershipRepository.findAllById_Post(post);
        List<ITUser> usersId = new ArrayList<>();
        for (Membership membership : memberships) {
            usersId.add(membership.getId().getITUser());
        }
        return usersId;
    }

    public List<ITUserDTO> getUsersInGroup(FKITGroupDTO group) {
        List<MembershipDTO> memberships = this.membershipRepository.findAllById_FkitGroup(group)
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
    public List<FKITGroup> getUsersGroupIds(ITUserDTO user) {    // should this return UUID? and not FKITGroup?
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(user);
        List<FKITGroup> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            groups.add(membership.getId().getFKITGroup());
        }
        return groups;
    }

    /**
     * finds which group the user has a specific post in.
     */
    public FKITGroup getGroupIdByUserAndPost(ITUserDTO user, Post post) {
        Membership membership = this.membershipRepository.findById_ItUserAndId_Post(user, post);
        return membership.getId().getFKITGroup();
    }

    public List<ITUser> getUserByGroupAndPost(FKITGroupDTO group, Post post) {
        List<Membership> memberships = this.membershipRepository.findAllById_FkitGroupAndId_Post(group, post);
        List<ITUser> users = new ArrayList<>();
        for (Membership membership : memberships) {
            users.add(membership.getId().getITUser());
        }
        return users;
    }

    public List<FKITGroup> getGroupsWithPost(Post post) {
        List<Membership> memberships = this.membershipRepository.findAllById_Post(post);
        List<FKITGroup> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            if (!groups.contains(membership.getId().getFKITGroup())) {
                groups.add(membership.getId().getFKITGroup());
            }
        }
        return groups;
    }

    public List<MembershipDTO> getMembershipsByUser(ITUserDTO user) {
        return this.membershipRepository.findAllById_ItUser(user).stream().map(Membership::toDTO).collect(Collectors.toList());
    }

    public MembershipDTO getMembershipByUserAndGroup(ITUserDTO user, FKITGroupDTO group) {
        return this.membershipRepository.findById_ItUserAndId_FkitGroup(user, group).toDTO();
    }

    public void removeUserFromGroup(FKITGroupDTO group, ITUserDTO user) {
        MembershipDTO membership = getMembershipByUserAndGroup(user, group);
        this.membershipRepository.delete(membership);
    }

    public void editMembership(MembershipDTO membership, String unofficialName, Post post) {
        membership.setUnofficialPostName(unofficialName);
        membership.getId().setPost(post);
        this.membershipRepository.save(membership);
    }

    public void removeAllUsersFromGroup(FKITGroupDTO group) {
        List<ITUserDTO> users = getUsersInGroup(group);
        for (ITUserDTO user : users) {
            this.membershipRepository.delete(this.getMembershipByUserAndGroup(user, group));
        }
    }

    public void removeAllMemberships(ITUserDTO user) {
        List<Membership> memberships = this.membershipRepository.findAllById_ItUser(user);
        memberships.forEach(this.membershipRepository::delete);
    }
}
