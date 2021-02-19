package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.data.Membership;
import it.chalmers.gamma.domain.membership.data.MembershipPK;
import it.chalmers.gamma.domain.membership.data.MembershipRepository;
import it.chalmers.gamma.domain.membership.data.MembershipShallowDTO;
import it.chalmers.gamma.domain.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;

import java.util.UUID;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipFinder membershipFinder;

    public MembershipService(MembershipRepository membershipRepository, MembershipFinder membershipFinder) {
        this.membershipRepository = membershipRepository;
        this.membershipFinder = membershipFinder;
    }

    public void addMembership(MembershipShallowDTO membership) throws GroupNotFoundException, PostNotFoundException, UserNotFoundException {
        this.membershipRepository.save(new Membership(membership));
    }

    public void removeMembership(UserId userId, UUID groupId, UUID postId) throws MembershipNotFoundException {
        if(!this.membershipRepository.existsById(new MembershipPK(postId, groupId, userId))) {
            throw new MembershipNotFoundException();
        }

        this.membershipRepository.deleteById(new MembershipPK(postId, groupId, userId));
    }

    public void editMembership(MembershipShallowDTO newEdit) throws MembershipNotFoundException, IDsNotMatchingException, GroupNotFoundException, PostNotFoundException, UserNotFoundException {
        Membership membership = this.membershipFinder.getMembershipEntityByUserGroupPost(newEdit.getUserId(), newEdit.getGroupId(), newEdit.getPostId());
        membership.apply(newEdit);
        this.membershipRepository.save(membership);
    }

}
