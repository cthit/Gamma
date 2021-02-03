package it.chalmers.gamma.membership.service;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.membership.data.Membership;
import it.chalmers.gamma.membership.data.MembershipPK;
import it.chalmers.gamma.membership.data.MembershipRepository;
import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.dto.MembershipShallowDTO;
import it.chalmers.gamma.membership.exception.MembershipNotFoundException;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.post.exception.PostNotFoundException;
import it.chalmers.gamma.user.dto.UserDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import it.chalmers.gamma.user.exception.UserNotFoundException;
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
