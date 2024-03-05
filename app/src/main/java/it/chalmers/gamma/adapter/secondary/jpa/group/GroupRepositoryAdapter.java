package it.chalmers.gamma.adapter.secondary.jpa.group;

import it.chalmers.gamma.adapter.secondary.jpa.supergroup.SuperGroupJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserEntityConverter;
import it.chalmers.gamma.adapter.secondary.jpa.user.UserJpaRepository;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorHelper;
import it.chalmers.gamma.adapter.secondary.jpa.util.PersistenceErrorState;
import it.chalmers.gamma.app.group.domain.*;
import it.chalmers.gamma.app.image.domain.ImageUri;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserMembership;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GroupRepositoryAdapter implements GroupRepository {

  private static final PersistenceErrorState SUPER_GROUP_NOT_FOUND =
      new PersistenceErrorState(
          "g_group_super_group_id_fkey", PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private static final PersistenceErrorState GROUP_NAME_ALREADY_EXISTS =
      new PersistenceErrorState("g_group_e_name_key", PersistenceErrorState.Type.NOT_UNIQUE);
  private static final PersistenceErrorState USER_NOT_FOUND =
      new PersistenceErrorState(
          "membership_user_id_fkey", PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private static final PersistenceErrorState POST_NOT_FOUND =
      new PersistenceErrorState(
          "membership_post_id_fkey", PersistenceErrorState.Type.FOREIGN_KEY_VIOLATION);
  private final GroupJpaRepository groupJpaRepository;
  private final GroupEntityConverter groupEntityConverter;
  private final MembershipJpaRepository membershipJpaRepository;
  private final PostEntityConverter postEntityConverter;
  private final SuperGroupJpaRepository superGroupJpaRepository;
  private final PostJpaRepository postJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final UserEntityConverter userEntityConverter;

  public GroupRepositoryAdapter(
      GroupJpaRepository groupJpaRepository,
      GroupEntityConverter groupEntityConverter,
      MembershipJpaRepository membershipJpaRepository,
      PostEntityConverter postEntityConverter,
      SuperGroupJpaRepository superGroupJpaRepository,
      PostJpaRepository postJpaRepository,
      UserJpaRepository userJpaRepository,
      UserEntityConverter userEntityConverter) {
    this.groupJpaRepository = groupJpaRepository;
    this.groupEntityConverter = groupEntityConverter;
    this.membershipJpaRepository = membershipJpaRepository;
    this.postEntityConverter = postEntityConverter;
    this.superGroupJpaRepository = superGroupJpaRepository;
    this.postJpaRepository = postJpaRepository;
    this.userJpaRepository = userJpaRepository;
    this.userEntityConverter = userEntityConverter;
  }

  @Override
  public void save(Group group) throws GroupNameAlreadyExistsException {
    try {
      this.groupJpaRepository.saveAndFlush(toEntity(group));
    } catch (DataIntegrityViolationException e) {
      PersistenceErrorState state = PersistenceErrorHelper.getState(e);

      if (state.equals(SUPER_GROUP_NOT_FOUND)) {
        throw new SuperGroupNotFoundRuntimeException();
      } else if (state.equals(GROUP_NAME_ALREADY_EXISTS)) {
        throw new GroupNameAlreadyExistsException();
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
    return this.groupJpaRepository.findAll().stream()
        .map(this.groupEntityConverter::toDomain)
        .toList();
  }

  @Override
  public List<Group> getAllBySuperGroup(SuperGroupId superGroupId) {
    return this.groupJpaRepository.findAllBySuperGroupId(superGroupId.value()).stream()
        .map(this.groupEntityConverter::toDomain)
        .toList();
  }

  @Override
  public List<Group> getAllByPost(PostId postId) {
    return this.membershipJpaRepository.findAllById_Post_Id(postId.value()).stream()
        .map(membershipEntity -> membershipEntity.getId().getGroup())
        .map(this.groupEntityConverter::toDomain)
        .distinct()
        .toList();
  }

  @Override
  public List<UserMembership> getAllByUser(UserId userId) {
    return this.membershipJpaRepository.findAllById_User_Id(userId.value()).stream()
        .map(
            membershipEntity ->
                new UserMembership(
                    this.postEntityConverter.toDomain(membershipEntity.getId().getPost()),
                    this.groupEntityConverter.toDomain(membershipEntity.getId().getGroup()),
                    new UnofficialPostName(membershipEntity.getUnofficialPostName())))
        .toList();
  }

  @Override
  public Optional<Group> get(GroupId groupId) {
    return this.groupJpaRepository
        .findById(groupId.value())
        .map(this.groupEntityConverter::toDomain);
  }

  @Override
  public List<GroupMember> getAllMembersBySuperGroup(SuperGroupId superGroupId) {
    return this.membershipJpaRepository.findAllBySuperGroup(superGroupId.value()).stream()
        .map(
            membershipEntity ->
                new GroupMember(
                    this.postEntityConverter.toDomain(membershipEntity.getId().getPost()),
                    new UnofficialPostName(membershipEntity.getUnofficialPostName()),
                    this.userEntityConverter.toDomain(membershipEntity.getId().getUser())))
        .toList();
  }

  private GroupEntity toEntity(Group group) {
    GroupEntity entity =
        this.groupJpaRepository.findById(group.id().value()).orElse(new GroupEntity());

    entity.increaseVersion(group.version());

    entity.id = group.id().getValue();
    entity.name = group.name().value();
    entity.prettyName = group.prettyName().value();
    entity.superGroup = superGroupJpaRepository.getById(group.superGroup().id().value());

    if (entity.members == null) {
      entity.members = new ArrayList<>();
    }

    entity.members.clear();
    entity.members.addAll(
        group.groupMembers().stream()
            .map(
                groupMember ->
                    new MembershipEntity(
                        new MembershipPK(
                            this.postJpaRepository.getById(groupMember.post().id().value()),
                            entity,
                            this.userJpaRepository.getById(groupMember.user().id().value())),
                        groupMember.unofficialPostName().value()))
            .toList());

    if (entity.groupImages == null) {
      entity.groupImages = new GroupImagesEntity();
    }

    entity.groupImages.group = entity;
    entity.groupImages.groupId = entity.id;
    entity.groupImages.avatarUri = group.avatarUri().map(ImageUri::value).orElse(null);
    entity.groupImages.bannerUri = group.bannerUri().map(ImageUri::value).orElse(null);

    return entity;
  }
}
