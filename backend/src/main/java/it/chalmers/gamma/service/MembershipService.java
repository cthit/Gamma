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
    public List<UUID> getPostHoldersIds(Post post){
        List<Membership> memberships = membershipRepository.findAllByPost_Id(post.getId());
        List<UUID> usersId = new ArrayList<>();
        for(Membership membership : memberships){
            usersId.add(membership.getId().getITUserId());
        }
        return usersId;
    }
    public List<UUID> getUsersGroupIds(ITUser user){
        List<Membership> memberships = membershipRepository.findAllById_ItuserId(user.getId());
        List<UUID> groups = new ArrayList<>();
        for(Membership membership : memberships){
            groups.add(membership.getId().getFKITGroupId());
        }
        return groups;
    }
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
