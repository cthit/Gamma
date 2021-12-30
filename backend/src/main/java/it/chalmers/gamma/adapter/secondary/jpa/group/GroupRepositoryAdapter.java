package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.util.DataIntegrityErrorState;
import it.chalmers.gamma.adapter.secondary.jpa.util.DataIntegrityViolationHelper;
import it.chalmers.gamma.app.client.domain.ClientRepository;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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

    private static final DataIntegrityErrorState SUPER_GROUP_NOT_FOUND = new DataIntegrityErrorState(
            "fkit_group_super_group_id_fkey",
            DataIntegrityErrorState.Type.NOT_FOUND
    );

    private static final DataIntegrityErrorState GROUP_NAME_ALREADY_EXISTS = new DataIntegrityErrorState(
            "fkit_group_e_name_key",
            DataIntegrityErrorState.Type.NOT_UNIQUE
    );

    private static final DataIntegrityErrorState USER_NOT_FOUND = new DataIntegrityErrorState(
            "membership_user_id_fkey",
            DataIntegrityErrorState.Type.NOT_FOUND
    );

    private static final DataIntegrityErrorState POST_NOT_FOUND = new DataIntegrityErrorState(
            "membership_post_id_fkey",
            DataIntegrityErrorState.Type.NOT_FOUND
    );

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
    public void save(Group group) throws GroupAlreadyExistsException {
        try {
            this.groupJpaRepository.saveAndFlush(groupEntityConverter.toEntity(group));
        } catch (DataIntegrityViolationException e) {
            DataIntegrityErrorState state = DataIntegrityViolationHelper.getState(e);

            if (state.equals(SUPER_GROUP_NOT_FOUND)) {
                throw new SuperGroupNotFoundRuntimeException();
            } else if (state.equals(GROUP_NAME_ALREADY_EXISTS)) {
                throw new GroupAlreadyExistsException();
            } else if (state.equals(USER_NOT_FOUND)) {
                throw new UserNotFoundRuntimeException();
            } else if (state.equals(POST_NOT_FOUND)) {
                throw new PostNotFoundRuntimeException();
            }

            throw e;
        }
    }

    @Override
    public void delete(GroupId groupId) throws GroupNotFoundException {
        try {
            this.groupJpaRepository.deleteById(groupId.value());
        } catch (EmptyResultDataAccessException e) {
            throw new GroupNotFoundException();
        }
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
    public List<UserMembership> getAllByUser(UserId userId) {
        return this.membershipJpaRepository.findAllById_User_Id(userId.value())
                .stream()
                .map(membershipEntity -> new UserMembership(
                        this.postEntityConverter.toDomain(membershipEntity.getId().getPost()),
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
