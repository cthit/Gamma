package it.chalmers.gamma.domain.membership.service;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.membership.data.db.Membership;
import it.chalmers.gamma.domain.membership.data.db.MembershipPK;
import it.chalmers.gamma.domain.membership.data.db.MembershipRepository;
import it.chalmers.gamma.domain.membership.data.dto.*;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.service.PostFinder;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.service.UserFinder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Component
public class MembershipFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembershipFinder.class);

    private final MembershipRepository membershipRepository;
    private final GroupFinder groupFinder;
    private final UserFinder userFinder;
    private final PostFinder postFinder;

    public MembershipFinder(MembershipRepository membershipRepository,
                            GroupFinder groupFinder,
                            UserFinder userFinder,
                            PostFinder postFinder) {
        this.membershipRepository = membershipRepository;
        this.groupFinder = groupFinder;
        this.userFinder = userFinder;
        this.postFinder = postFinder;
    }


    public List<GroupDTO> getGroupsWithPost(PostId postId) {
        List<GroupId> groups = this.membershipRepository.findAllById_PostId(postId)
                .stream()
                .map(Membership::toDTO)
                .map(MembershipShallowDTO::getGroupId)
                .collect(Collectors.toList());

        return new HashSet<>(groups)
                .stream()
                .map(this::getGroup)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByUser(UserId userId) throws EntityNotFoundException {
        if(!this.userFinder.exists(userId)) {
            throw new EntityNotFoundException();
        }

        List<Membership> memberships = this.membershipRepository
                .findAllById_UserId(userId);
        return memberships
                .stream()
                .map(Membership::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByGroup(GroupId groupId) throws EntityNotFoundException {
        if(!this.groupFinder.exists(groupId)) {
            throw new EntityNotFoundException();
        }

        List<Membership> memberships = this.membershipRepository.findAllById_GroupId(groupId);

        return memberships
                .stream()
                .map(Membership::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByGroupAndPost(GroupId groupId, PostId postId) throws EntityNotFoundException {
        if (!this.groupFinder.exists(groupId) || this.postFinder.exists(postId)) {
            throw new EntityNotFoundException();
        }

        List<Membership> memberships = this.membershipRepository.findAllById_GroupIdAndId_PostId(groupId, postId);

        return memberships
                .stream()
                .map(Membership::toDTO)
                .map(this::fromShallow)
                .collect(Collectors.toList());
    }

    private GroupDTO getGroup(GroupId groupId) {
        try{
            return this.groupFinder.get(groupId);
        } catch(EntityNotFoundException e) {
            LOGGER.error("Group with id " + groupId + " not found", e);
        }

        return null;
    }

    protected Membership getMembershipEntityByUserGroupPost(UserId userId, GroupId groupId, PostId postId) throws EntityNotFoundException {
        return this.membershipRepository.findById(new MembershipPK(postId, groupId, userId))
                .orElseThrow(EntityNotFoundException::new);
    }

    protected MembershipDTO fromShallow(MembershipShallowDTO membership) {
        try {
            return new MembershipDTO.MembershipDTOBuilder()
                    .group(this.groupFinder.get(membership.getGroupId()))
                    .user(this.userFinder.get(membership.getUserId()))
                    .post(this.postFinder.get(membership.getPostId()))
                    .unofficialPostName(membership.getUnofficialPostName())
                    .build();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
