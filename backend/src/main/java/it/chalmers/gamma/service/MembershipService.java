package it.chalmers.gamma.service;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.pk.MembershipPK;
import it.chalmers.gamma.db.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.time.Year;

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
        System.out.println("trying to add: " + membership.toString());
        membershipRepository.save(membership);
    }
}
