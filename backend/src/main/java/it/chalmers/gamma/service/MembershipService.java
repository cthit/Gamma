package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }


    //TODO check which methods should be left in this class,
    // many are probably never going to be used.

    //TODO this should perhaps be rewritten to return actual objects instead of the ID:s of objects.


    /**
     * adds a user to the group.
     *
     * @param group    group the user should be added to
     * @param user     which user is added
     * @param post     what post the user has in group
     * @param postname what the unoficial-post name is
     * @param year     which group-year the user is added in
     */
    public void addUserToGroup(FKITGroup group, ITUser user, Post post, String postname, Year year) {
        Membership membership = new Membership();
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroup(group);
        pk.setITUser(user);
        membership.setId(pk);
        membership.setPost(post);
        membership.setUnofficialPostName(postname);
        membership.setYear(year);
        this.membershipRepository.save(membership);
    }

    //should this return UUID? and not ITUser?.

    /**
     * finds all users that has a specific post.
     *
     * @param post which post that should be looked for
     * @return the users UUID, the identifier for the user
     */
    public List<ITUser> getPostHoldersIds(Post post) {
        List<Membership> memberships = this.membershipRepository.findAllByPost(post);
        List<ITUser> usersId = new ArrayList<>();
        for (Membership membership : memberships) {
            usersId.add(membership.getId().getITUser());
        }
        return usersId;
    }

    public List<ITUser> getUsersInGroup(FKITGroup group) {
        List<Membership> memberships = this.membershipRepository.findAllById_FkitGroup(group);
        List<ITUser> groupIds = new ArrayList<>();
        for (Membership membership : memberships) {
            groupIds.add(membership.getId().getITUser());
        }
        return groupIds;
    }

    /**
     * gets which groups a user is a part of.
     *
     * @param user which user which group membersships should be queried
     * @return The UUIDs of the groups the user is a part of
     */
    public List<FKITGroup> getUsersGroupIds(ITUser user) {    // should this return UUID? and not FKITGroup?
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
    public FKITGroup getGroupIdByUserAndPost(ITUser user, Post post) {
        Membership membership = this.membershipRepository.findById_ItUserAndPost(user, post);
        return membership.getId().getFKITGroup();
    }

    public List<ITUser> getUserByGroupAndPost(FKITGroup group, Post post) {
        List<Membership> memberships = this.membershipRepository.findAllById_FkitGroupAndPost(group, post);
        List<ITUser> users = new ArrayList<>();
        for (Membership membership : memberships) {
            users.add(membership.getId().getITUser());
        }
        return users;
    }

    public List<FKITGroup> getGroupsWithPost(Post post) {
        List<Membership> memberships = this.membershipRepository.findAllByPost(post);
        List<FKITGroup> groups = new ArrayList<>();
        for (Membership membership : memberships) {
            if (!groups.contains(membership.getId().getFKITGroup())) {
                groups.add(membership.getId().getFKITGroup());
            }
        }
        return groups;
    }

    public List<Membership> getMembershipsByUser(ITUser user) {
        return this.membershipRepository.findAllById_ItUser(user);
    }

    public Membership getMembershipByUserAndGroup(ITUser user, FKITGroup group) {
        return this.membershipRepository.findById_ItUserAndId_FkitGroup(user, group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MembershipService that = (MembershipService) o;
        return this.membershipRepository.equals(that.membershipRepository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.membershipRepository);
    }

    @Override
    public String toString() {
        return "MembershipService{"
            + "membershipRepository=" + this.membershipRepository
            + '}';
    }
}
