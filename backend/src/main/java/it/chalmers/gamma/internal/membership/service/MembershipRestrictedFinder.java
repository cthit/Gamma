package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.post.service.PostId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MembershipRestrictedFinder {

    private final MembershipFinder membershipFinder;
    private final MembershipRepository membershipRepository;

    public MembershipRestrictedFinder(MembershipFinder membershipFinder,
                                      MembershipRepository membershipRepository) {
        this.membershipFinder = membershipFinder;
        this.membershipRepository = membershipRepository;
    }

    public List<MembershipRestrictedDTO> getRestrictedMembershipsInGroup(GroupId groupId) {
        return this.membershipRepository
                .findAllById_GroupId(groupId)
                .stream()
                .map(this::toMembershipRestrictedDTO)
                .collect(Collectors.toList());
    }

    public List<MembershipRestrictedDTO> getUserByGroupAndPost(GroupId groupId, PostId postId) {
        return this.membershipRepository
                .findAllById_GroupIdAndId_PostId(groupId, postId)
                .stream()
                .map(this::toMembershipRestrictedDTO)
                .collect(Collectors.toList());
    }

    private MembershipRestrictedDTO toMembershipRestrictedDTO(Membership membership) {
        return new MembershipRestrictedDTO(this.membershipFinder.fromShallow(membership.toDTO()));
    }

}
