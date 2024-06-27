package it.chalmers.gamma.app.group;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.group.domain.*;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GroupFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(GroupFacade.class);

  private final GroupRepository groupRepository;
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final SuperGroupRepository superGroupRepository;

  public GroupFacade(
      AccessGuard accessGuard,
      GroupRepository groupRepository,
      UserRepository userRepository,
      PostRepository postRepository,
      SuperGroupRepository superGroupRepository) {
    super(accessGuard);
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.superGroupRepository = superGroupRepository;
  }

  public UUID create(NewGroup newGroup) {
    accessGuard.require(isAdmin());

    Group group =
        new Group(
            GroupId.generate(),
            0,
            new Name(newGroup.name),
            new PrettyName(newGroup.prettyName),
            this.superGroupRepository
                .get(new SuperGroupId(newGroup.superGroup))
                .orElseThrow(SuperGroupNotFoundRuntimeException::new),
            new ArrayList<>(),
            Optional.empty(),
            Optional.empty());

    this.groupRepository.save(group);

    return group.id().value();
  }

  @Transactional
  public void update(UpdateGroup updateGroup) {
    accessGuard.require(isAdmin());

    GroupId groupId = new GroupId(updateGroup.id);
    Group oldGroup =
        this.groupRepository.get(groupId).orElseThrow(GroupNotFoundRuntimeException::new);
    Group newGroup =
        oldGroup
            .with()
            .version(updateGroup.version)
            .name(new Name(updateGroup.name))
            .prettyName(new PrettyName(updateGroup.prettyName))
            .superGroup(
                this.superGroupRepository
                    .get(new SuperGroupId(updateGroup.superGroup))
                    .orElseThrow(SuperGroupNotFoundRuntimeException::new))
            .build();

    this.groupRepository.save(newGroup);
  }

  public void setMembers(UUID groupId, List<ShallowMember> newMembers)
      throws GroupNotFoundRuntimeException {
    accessGuard.require(isAdmin());

    Group oldGroup =
        this.groupRepository
            .get(new GroupId(groupId))
            .orElseThrow(GroupNotFoundRuntimeException::new);

    List<GroupMember> newGroupMembers = new ArrayList<>();

    for (ShallowMember shallowMember : newMembers) {
      newGroupMembers.add(
          new GroupMember(
              this.postRepository
                  .get(new PostId(shallowMember.postId))
                  .orElseThrow(PostNotFoundRuntimeException::new),
              new UnofficialPostName(shallowMember.unofficialPostName),
              this.userRepository
                  .get(new UserId(shallowMember.userId))
                  .orElseThrow(UserNotFoundRuntimeException::new)));
    }

    this.groupRepository.save(oldGroup.withGroupMembers(newGroupMembers));
  }

  @Transactional
  public void changeUnofficialPostName(
      UUID groupId, UUID postId, UUID userId, String newUnofficialPostName) {
    accessGuard.requireEither(isAdmin(), isMe(new UserId(userId)));

    Group group =
        this.groupRepository
            .get(new GroupId(groupId))
            .orElseThrow(GroupNotFoundRuntimeException::new);

    List<GroupMember> groupMembers = new ArrayList<>(group.groupMembers());
    boolean found = false;
    for (int i = 0; i < groupMembers.size() && !found; i++) {
      GroupMember groupMember = groupMembers.get(i);
      if (groupMember.user().id().value().equals(userId)
          && groupMember.post().id().value().equals(postId)) {
        GroupMember newGroupMember =
            groupMember.withUnofficialPostName(new UnofficialPostName(newUnofficialPostName));
        groupMembers.set(i, newGroupMember);
        found = true;
      }
    }

    if (!found) {
      throw new PostNotFoundRuntimeException();
    }

    group = group.withGroupMembers(groupMembers);
    this.groupRepository.save(group);
  }

  @Transactional
  public void delete(UUID id) throws GroupNotFoundRuntimeException {
    accessGuard.require(isAdmin());

    try {
      this.groupRepository.delete(new GroupId(id));
    } catch (GroupRepository.GroupNotFoundException e) {
      throw new GroupNotFoundRuntimeException();
    }
  }

  public boolean groupWithNameAlreadyExists(UUID id, String name) {
    accessGuard.require(isSignedIn());

    Optional<Group> maybeGroup = this.groupRepository.get(new Name(name));

    return maybeGroup.filter(group -> !group.id().value().equals(id)).isPresent();
  }

  public Optional<GroupWithMembersDTO> getWithMembers(UUID groupId) {
    accessGuard.require(isSignedIn());

    return this.groupRepository.get(new GroupId(groupId)).map(GroupWithMembersDTO::new);
  }

  public List<GroupDTO> getAll() {
    accessGuard.requireEither(isSignedIn(), isClientApi());

    return this.groupRepository.getAll().stream().map(GroupDTO::new).toList();
  }

  public Optional<GroupDTO> get(UUID id) {
    accessGuard.requireEither(isSignedIn());

    return this.groupRepository.get(new GroupId(id)).map(GroupDTO::new);
  }

  public List<GroupWithMembersDTO> getAllBySuperGroup(UUID superGroupId) {
    accessGuard.require(isSignedIn());

    return this.groupRepository.getAllBySuperGroup(new SuperGroupId(superGroupId)).stream()
        .map(GroupWithMembersDTO::new)
        .toList();
  }

  public record NewGroup(String name, String prettyName, UUID superGroup) {}

  public record UpdateGroup(
      UUID id, int version, String name, String prettyName, UUID superGroup) {}

  public record ShallowMember(UUID userId, UUID postId, String unofficialPostName) {}

  public record GroupMemberDTO(
      UserFacade.UserDTO user, PostFacade.PostDTO post, String unofficialPostName) {
    public GroupMemberDTO(GroupMember groupMember) {
      this(
          new UserFacade.UserDTO(groupMember.user()),
          new PostFacade.PostDTO(groupMember.post()),
          groupMember.unofficialPostName().value());
    }
  }

  public record GroupDTO(
      UUID id,
      String name,
      String prettyName,
      SuperGroupFacade.SuperGroupDTO superGroup,
      int version) {
    public GroupDTO(Group group) {
      this(
          group.id().value(),
          group.name().value(),
          group.prettyName().value(),
          new SuperGroupFacade.SuperGroupDTO(group.superGroup()),
          group.version());
    }
  }

  public record GroupWithMembersDTO(
      UUID id,
      int version,
      String name,
      String prettyName,
      List<GroupMemberDTO> groupMembers,
      SuperGroupFacade.SuperGroupDTO superGroup) {
    public GroupWithMembersDTO(Group group) {
      this(
          group.id().value(),
          group.version(),
          group.name().value(),
          group.prettyName().value(),
          group.groupMembers().stream().map(GroupMemberDTO::new).toList(),
          new SuperGroupFacade.SuperGroupDTO(group.superGroup()));
    }
  }

  public static class UserNotFoundRuntimeException extends RuntimeException {}

  public static class PostNotFoundRuntimeException extends RuntimeException {}

  public static class GroupNotFoundRuntimeException extends RuntimeException {}

  public static class SuperGroupNotFoundRuntimeException extends RuntimeException {}
}
