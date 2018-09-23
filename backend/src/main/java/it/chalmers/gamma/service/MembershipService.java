package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MembershipService {
    private final MembershipRepository membershipRepository;
    public MembershipService(MembershipRepository membershipRepository){
        this.membershipRepository = membershipRepository;
    }


    //TODO check which methods should be left in this class, many are probably never going to be used.
    //TODO this should perhaps be rewritten to return actual objects instead of the ID:s of objects


    /**
     * adds a user to the group
     * @param group group the user should be added to
     * @param user  which user is added
     * @param post  what post the user has in group
     * @param postname  what the unoficial-post name is
     * @param year      which group-year the user is added in
     */
    public void addUserToGroup(FKITGroup group, ITUser user, Post post, String postname, Year year){
        Membership membership = new Membership();
        MembershipPK pk = new MembershipPK();
        pk.setFKITGroupId(group.getId());
        pk.setITUserId(user.getId());
        membership.setId(pk);
        membership.setPost(post);
        membership.setUnofficialPostName(postname);
        membership.setYear(year);
        membershipRepository.save(membership);
    }

    /**
     * finds all users that has a specific post
     * @param post which post that should be looked for
     * @return the users UUID, the identifier for the user
     */
    public List<UUID> getPostHoldersIds(Post post){     //should this return UUID? and not ITUser?
        List<Membership> memberships = membershipRepository.findAllByPost_Id(post.getId());
        List<UUID> usersId = new ArrayList<>();
        for(Membership membership : memberships){
            usersId.add(membership.getId().getITUserId());
        }
        return usersId;
    }
    public List<UUID> getUsersInGroup(FKITGroup group){
        List<Membership> memberships = membershipRepository.findAllById_FkitGroupId(group.getId());
        List<UUID> groupIds = new ArrayList<>();
        for (Membership membership : memberships){
            groupIds.add(membership.getId().getITUserId());
        }
        return groupIds;
    }

    /**
     * gets which groups a user is a part of
     * @param user which user which group membersships should be queried
     * @return  The UUIDs of the groups the user is a part of
     */
    public List<UUID> getUsersGroupIds(ITUser user){    // should this return UUID? and not FKITGroup?
        List<Membership> memberships = membershipRepository.findAllById_ItuserId(user.getId());
        List<UUID> groups = new ArrayList<>();
        for(Membership membership : memberships){
            groups.add(membership.getId().getFKITGroupId());
        }
        return groups;
    }

    /**
     * finds which group the user has a specific post in
     * @param user
     * @param post
     * @return
     */
    public UUID getGroupIdByUserAndPost(ITUser user, Post post){
        Membership membership = membershipRepository.findById_ItuserIdAndPost(user.getId(), post);
        return membership.getId().getFKITGroupId();
    }
    public List<UUID>getUserIdsByGroupAndPost(FKITGroup group, Post post){
        List<Membership> memberships = membershipRepository.findAllById_FkitGroupIdAndPost(group.getId(), post);
        List<UUID> users = new ArrayList<>();
        for(Membership membership : memberships){
            users.add(membership.getId().getITUserId());
        }
        return users;
    }
    public List<UUID> getGroupsWithPost(Post post){
        List<Membership> memberships = membershipRepository.findAllByPost_Id(post.getId());
        List<UUID> groups = new ArrayList<>();
        for(Membership membership : memberships){
            if(!groups.contains(membership.getId().getFKITGroupId())) {
                groups.add(membership.getId().getFKITGroupId());
            }
        }
        return groups;
    }
}
