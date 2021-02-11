package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.data.Membership;
import it.chalmers.gamma.domain.membership.data.MembershipRepository;
import it.chalmers.gamma.domain.membership.data.MembershipShallowDTO;
import it.chalmers.gamma.domain.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;

import java.util.UUID;

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

    public void addUserToGroup(MembershipShallowDTO membership) throws GroupNotFoundException, PostNotFoundException, UserNotFoundException {
        this.membershipRepository.save(new Membership(membershipFinder.fromShallow(membership)));
    }

    public void removeUserFromGroup(UUID userId, UUID groupId) throws MembershipNotFoundException {
        if(!this.membershipRepository.existsById_UserIdAndId_GroupId(userId, groupId)) {
            throw new MembershipNotFoundException();
        }

        this.membershipRepository.deleteById_UserIdAndId_GroupId(userId, groupId);
    }

    public void editMembership(MembershipShallowDTO newEdit) throws MembershipNotFoundException, IDsNotMatchingException, GroupNotFoundException, PostNotFoundException, UserNotFoundException {
        Membership membership = this.membershipFinder.getMembershipEntityByUserAndGroup(newEdit.getUserId(), newEdit.getGroupId());
        membership.apply(this.membershipFinder.fromShallow(newEdit));
        this.membershipRepository.save(membership);
    }

    public boolean groupIsActiveCommittee(GroupDTO group) {
        return group.isActive();
    }

    public boolean isPostUsed(UUID postId) {
        return !this.membershipRepository.findAllById_PostId(postId).isEmpty();
    }
}
