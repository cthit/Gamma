package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
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
    private final PostEntityConverter postEntityConverter;

    public GroupRepositoryAdapter(GroupJpaRepository groupJpaRepository,
                                  GroupEntityConverter groupEntityConverter,
                                  MembershipJpaRepository membershipJpaRepository,
                                  PostEntityConverter postEntityConverter) {
        this.groupJpaRepository = groupJpaRepository;
        this.groupEntityConverter = groupEntityConverter;
        this.membershipJpaRepository = membershipJpaRepository;
        this.postEntityConverter = postEntityConverter;
    }

    @Override
    public void save(Group group) {
        this.groupJpaRepository.saveAndFlush(groupEntityConverter.toEntity(group));
    }

    @Override
    public void delete(GroupId groupId) throws GroupNotFoundException {
        this.groupJpaRepository.deleteById(groupId.value());
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
                .map(membershipEntity -> membershipEntity.getId().getGroup())
                .map(this.groupEntityConverter::toDomain)
                .distinct()
                .toList();
    }

    @Override
    public List<UserMembership> getGroupsByUser(UserId userId) {
        return this.membershipJpaRepository.findAllById_User_Id(userId.value())
                .stream()
                .map(membershipEntity -> new UserMembership(
                        postEntityConverter.toDomain(membershipEntity.getId().getPost()),
                        this.groupEntityConverter.toDomain(membershipEntity.getId().getGroup()),
                        new UnofficialPostName(membershipEntity.getUnofficialPostName())
                ))
                .toList();
    }

    @Override
    public Optional<Group> get(GroupId groupId) {
        return this.groupJpaRepository.findById(groupId.value()).map(this.groupEntityConverter::toDomain);
    }
}
