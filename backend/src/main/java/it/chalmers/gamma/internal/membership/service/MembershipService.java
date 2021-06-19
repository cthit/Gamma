package it.chalmers.gamma.internal.membership.service;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.domain.Membership;
import it.chalmers.gamma.domain.PostId;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.post.service.PostService;
import it.chalmers.gamma.domain.UserRestricted;
import it.chalmers.gamma.internal.user.service.UserService;
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

    protected Membership fromShallow(MembershipShallowDTO membership) {
        try {
            return new Membership(
                    this.postService.get(membership.postId()),
                    this.groupService.get(membership.groupId()),
                    membership.unofficialPostName(),
                    new UserRestricted(this.userService.get(membership.userId()))
            );
        } catch (PostService.PostNotFoundException e) {
            e.printStackTrace();
        } catch (GroupService.GroupNotFoundException e) {
            e.printStackTrace();
        } catch (UserService.UserNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class MembershipNotFoundException extends Exception { }


}
