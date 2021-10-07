package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.app.domain.user.UserMembership;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupRepositoryAdapter implements GroupRepository {

    private final GroupJpaRepository groupJpaRepository;
    private final GroupEntityConverter groupEntityConverter;
    private final MembershipJpaRepository membershipJpaRepository;

    public GroupRepositoryAdapter(GroupJpaRepository groupJpaRepository,
                                  GroupEntityConverter groupEntityConverter,
                                  MembershipJpaRepository membershipJpaRepository) {
        this.groupJpaRepository = groupJpaRepository;
        this.groupEntityConverter = groupEntityConverter;
        this.membershipJpaRepository = membershipJpaRepository;
    }

    @Override
    public void save(Group group) {
        this.groupJpaRepository.save(groupEntityConverter.toEntity(group));
    }

    @Override
    public void delete(GroupId groupId) throws GroupNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Group> getAll() {
        return this.groupJpaRepository.findAll().stream().map(this.groupEntityConverter::toDomain).toList();
    }

    @Override
    public List<Group> getAllBySuperGroup(SuperGroupId superGroupId) {
        return this.groupJpaRepository.findAllBySuperGroupId(superGroupId.value())
                .stream()
                .map(this.groupEntityConverter::toDomain)
                .toList();
    }

    @Override
    public List<Group> getAllByPost(PostId postId) {
        return this.membershipJpaRepository.findAllById_Post_Id(postId.value())
                .stream()
                .map(membershipEntity -> membershipEntity.id().getGroup())
                .map(this.groupEntityConverter::toDomain)
                .distinct()
                .toList();
    }

    @Override
    public List<UserMembership> getGroupsByUser(UserId userId) {
        return this.membershipJpaRepository.findAllById_User_Id(userId.value())
                .stream()
                .map(membershipEntity -> new UserMembership(
                        membershipEntity.id().getPost().toDomain(),
                        this.groupEntityConverter.toDomain(membershipEntity.id().getGroup()),
                        new UnofficialPostName(membershipEntity.getUnofficialPostName())
                ))
                .toList();
    }

    @Override
    public Optional<Group> get(GroupId groupId) {
        return this.groupJpaRepository.findById(groupId.value()).map(this.groupEntityConverter::toDomain);
    }
}
