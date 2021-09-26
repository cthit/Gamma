package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.group.GroupRepository;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.UnofficialPostName;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.domain.user.UserMembership;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GroupRepositoryAdapter implements GroupRepository {

    private final GroupJpaRepository groupRepository;
    private final GroupEntityConverter groupEntityConverter;
    private final MembershipJpaRepository membershipJpaRepository;

    public GroupRepositoryAdapter(GroupJpaRepository groupRepository,
                                  GroupEntityConverter groupEntityConverter,
                                  MembershipJpaRepository membershipJpaRepository) {
        this.groupRepository = groupRepository;
        this.groupEntityConverter = groupEntityConverter;
        this.membershipJpaRepository = membershipJpaRepository;
    }

    @Override
    public void create(Group group) {
        this.groupRepository.saveAndFlush(groupEntityConverter.toEntity(group));
    }

    @Override
    public void save(Group group) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(GroupId groupId) throws GroupNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Group> getAll() {
        return this.groupRepository.findAll().stream().map(this.groupEntityConverter::toDomain).toList();
    }

    @Override
    public List<Group> getAllBySuperGroup(SuperGroupId superGroupId) {
        throw new UnsupportedOperationException();
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
        return this.groupRepository.findById(groupId.value()).map(this.groupEntityConverter::toDomain);
    }
}
