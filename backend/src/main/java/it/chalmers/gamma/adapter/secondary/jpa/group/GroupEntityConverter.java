package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntity;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupMember;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final MembershipJpaRepository membershipJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final PostJpaRepository postJpaRepository;

    public GroupEntityConverter(UserEntityConverter userEntityConverter,
                                MembershipJpaRepository membershipJpaRepository,
                                UserJpaRepository userJpaRepository,
                                GroupJpaRepository groupJpaRepository,
                                PostJpaRepository postJpaRepository) {
        this.userEntityConverter = userEntityConverter;
        this.membershipJpaRepository = membershipJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.groupJpaRepository = groupJpaRepository;
        this.postJpaRepository = postJpaRepository;
    }

    public GroupEntity toEntity(Group group) {
        GroupEntity entity = new GroupEntity();
        entity.id = group.id().getValue();
        entity.name = group.name().value();
        entity.prettyName = group.prettyName().value();
        entity.email = group.email().value();
        entity.superGroup = new SuperGroupEntity(group.superGroup());
        entity.members = group.groupMembers()
                .stream()
                .map(groupMember -> new MembershipEntity(
                        new MembershipPK(
                                this.postJpaRepository.getOne(groupMember.post().id().value()),
                                entity,
                                this.userJpaRepository.getOne(groupMember.user().id().value())),
                        groupMember.unofficialPostName().value()
                )).toList();
        return entity;
    }

    public Group toDomain(GroupEntity groupEntity) {
        //TODO: add imageuri and members
        GroupEntity.GroupBase b = groupEntity.toDomain();
        List<GroupMember> members = groupEntity.getMembers()
                .stream()
                .map(membershipEntity -> new GroupMember(
                        membershipEntity.id().getPost().toDomain(),
                        new UnofficialPostName(membershipEntity.getUnofficialPostName()),
                        this.userEntityConverter.toDomain(membershipEntity.id().getUser())
                ))
                .toList();

        return new Group(
                b.groupId(),
                b.email(),
                b.name(),
                b.prettyName(),
                b.superGroup(),
                members,
                ImageUri.nothing(),
                ImageUri.nothing()
        );
    }

}
