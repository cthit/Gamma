package it.chalmers.gamma.app.group;

import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipEntity;
import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipPK;
import it.chalmers.gamma.adapter.secondary.jpa.group.MembershipRepository;
import it.chalmers.gamma.app.domain.GroupId;
import it.chalmers.gamma.app.domain.Membership;
import it.chalmers.gamma.app.domain.PostId;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.post.PostService;
import it.chalmers.gamma.app.user.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserService userService;
    private final PostService postService;
    private final GroupService groupService;

    public MembershipService(MembershipRepository membershipRepository,
                             UserService userService,
                             PostService postService,
                             GroupService groupService) {
        this.membershipRepository = membershipRepository;
        this.userService = userService;
        this.postService = postService;
        this.groupService = groupService;
    }

    public void create(MembershipShallowDTO membership) {
        this.membershipRepository.save(new MembershipEntity(membership));
    }

    public void delete(MembershipPK membershipPK) {
        this.membershipRepository.deleteById(membershipPK);
    }

    public void update(MembershipShallowDTO newEdit) throws MembershipNotFoundException {
        MembershipEntity membership = this.getMembershipEntityByUserGroupPost(
                newEdit.userId(),
                newEdit.groupId(),
                newEdit.postId()
        );
        membership.apply(newEdit);
        this.membershipRepository.save(membership);
    }

    public List<Group> getGroupsWithPost(PostId postId) {
        List<GroupId> groups = this.membershipRepository.findAllById_PostId(postId)
                .stream()
                .map(MembershipEntity::toDTO)
                .map(MembershipShallowDTO::groupId)
                .collect(Collectors.toList());

        return new HashSet<>(groups)
                .stream()
                .map(this::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsByUser(UserId userId) {
        List<MembershipEntity> memberships = this.membershipRepository.findAllById_UserId(userId);

        return memberships
                .stream()
                .map(MembershipEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsByGroup(GroupId groupId) {
        List<MembershipEntity> memberships = this.membershipRepository.findAllById_GroupId(groupId);

        return memberships
                .stream()
                .map(MembershipEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsByGroupAndPost(GroupId groupId, PostId postId) {
        List<MembershipEntity> memberships = this.membershipRepository.findAllById_GroupIdAndId_PostId(groupId, postId);

        return memberships
                .stream()
                .map(MembershipEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<Membership> getMembershipsInGroup(GroupId groupId) {
        return this.membershipRepository
                .findAllById_GroupId(groupId)
                .stream()
                .map(MembershipEntity::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private Group getGroup(GroupId groupId) {
        try {
            return this.groupService.get(groupId);
        } catch (GroupService.GroupNotFoundException e) {
            return null;
        }
    }

    protected MembershipEntity getMembershipEntityByUserGroupPost(UserId userId, GroupId groupId, PostId postId) throws MembershipNotFoundException {
        return this.membershipRepository.findById(new MembershipPK(postId, groupId, userId))
                .orElseThrow(MembershipNotFoundException::new);
    }

    public static class MembershipNotFoundException extends Exception { }


}
