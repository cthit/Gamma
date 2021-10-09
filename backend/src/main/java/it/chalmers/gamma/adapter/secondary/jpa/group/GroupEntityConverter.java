package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupMember;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import it.chalmers.gamma.app.domain.user.Name;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupEntityConverter {

    private final UserEntityConverter userEntityConverter;
    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PostEntityConverter postEntityConverter;
    private final GroupJpaRepository groupJpaRepository;
    private final SuperGroupJpaRepository superGroupJpaRepository;
    private final SuperGroupEntityConverter superGroupEntityConverter;

    public GroupEntityConverter(UserEntityConverter userEntityConverter,
                                UserJpaRepository userJpaRepository,
                                PostJpaRepository postJpaRepository,
                                PostEntityConverter postEntityConverter,
                                GroupJpaRepository groupJpaRepository,
                                SuperGroupJpaRepository superGroupJpaRepository,
                                SuperGroupEntityConverter superGroupEntityConverter) {
        this.userEntityConverter = userEntityConverter;
        this.userJpaRepository = userJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.postEntityConverter = postEntityConverter;
        this.groupJpaRepository = groupJpaRepository;
        this.superGroupJpaRepository = superGroupJpaRepository;
        this.superGroupEntityConverter = superGroupEntityConverter;
    }

    public GroupEntity toEntity(Group group) {
        GroupEntity entity = this.groupJpaRepository.findById(group.id().value())
                .orElse(new GroupEntity());

        //TODO: Use this everywhere when converting to an entity
        //Is this even the correct place? Maybe should be in adapters
        entity.throwIfNotValidVersion(group.version());

        entity.id = group.id().getValue();
        entity.name = group.name().value();
        entity.prettyName = group.prettyName().value();
        entity.email = group.email().value();
        entity.superGroup = superGroupJpaRepository.getOne(group.superGroup().id().value());
        entity.members.clear();
        entity.members.addAll(group.groupMembers()
                .stream()
                .map(groupMember -> new MembershipEntity(
                        new MembershipPK(
                                this.postJpaRepository.getOne(groupMember.post().id().value()),
                                entity,
                                this.userJpaRepository.getOne(groupMember.user().id().value())),
                        groupMember.unofficialPostName().value()
                )).toList());

        return entity;
    }

    public Group toDomain(GroupEntity entity) {
        //TODO: add imageuri and members
        List<GroupMember> members = entity.getMembers()
                .stream()
                .map(membershipEntity -> new GroupMember(
                        this.postEntityConverter.toDomain(membershipEntity.id().getPost()),
                        new UnofficialPostName(membershipEntity.getUnofficialPostName()),
                        this.userEntityConverter.toDomain(membershipEntity.id().getUser())
                ))
                .toList();

        return new Group(
                entity.id(),
                entity.getVersion(),
                new Email(entity.email),
                new Name(entity.name),
                new PrettyName(entity.prettyName),
                this.superGroupEntityConverter.toDomain(entity.superGroup),
                members,
                Optional.empty(),
                Optional.empty()
        );
    }

}
