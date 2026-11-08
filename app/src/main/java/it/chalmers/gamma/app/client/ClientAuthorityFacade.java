package it.chalmers.gamma.app.client;

import static it.chalmers.gamma.app.authentication.AccessGuard.*;

import it.chalmers.gamma.app.Facade;
import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.client.domain.authority.Authority;
import it.chalmers.gamma.app.client.domain.authority.AuthorityName;
import it.chalmers.gamma.app.client.domain.authority.AuthoritySuperGroupPost;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthorityFacade extends Facade {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientAuthorityFacade.class);

  private final ClientAuthorityRepository clientAuthorityRepository;
  private final UserRepository userRepository;
  private final SuperGroupRepository superGroupRepository;
  private final PostRepository postRepository;

  public ClientAuthorityFacade(
      AccessGuard accessGuard,
      ClientAuthorityRepository clientAuthorityRepository,
      UserRepository userRepository,
      SuperGroupRepository superGroupRepository,
      PostRepository postRepository) {
    super(accessGuard);
    this.clientAuthorityRepository = clientAuthorityRepository;
    this.userRepository = userRepository;
    this.superGroupRepository = superGroupRepository;
    this.postRepository = postRepository;
  }

  public void create(UUID clientUid, String name)
      throws ClientAuthorityRepository.ClientAuthorityAlreadyExistsException {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    this.clientAuthorityRepository.create(uid, new AuthorityName(name));
  }

  public void delete(UUID clientUid, String name) throws ClientAuthorityNotFoundException {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    try {
      this.clientAuthorityRepository.delete(uid, new AuthorityName(name));
    } catch (ClientAuthorityRepository.ClientAuthorityNotFoundException e) {
      throw new ClientAuthorityNotFoundException();
    }
  }

  public Optional<ClientAuthorityDTO> get(UUID clientUid, String name) {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    return this.clientAuthorityRepository
        .get(uid, new AuthorityName(name))
        .map(ClientAuthorityDTO::new);
  }

  /**
   * Replaces who has the authority: the members of the super groups, the members of the super
   * groups' groups who hold the given posts, and the users.
   */
  @Transactional
  public void setMembers(UUID clientUid, String name, AuthorityMembers members)
      throws ClientAuthorityNotFoundException,
          SuperGroupNotFoundException,
          PostNotFoundException,
          UserNotFoundException {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    Authority authority =
        this.clientAuthorityRepository
            .get(uid, new AuthorityName(name))
            .orElseThrow(ClientAuthorityNotFoundException::new);

    List<SuperGroup> superGroups = new ArrayList<>();
    for (UUID superGroupId : distinct(members.superGroupIds())) {
      superGroups.add(getSuperGroup(superGroupId));
    }

    List<AuthoritySuperGroupPost> superGroupPosts = new ArrayList<>();
    for (SuperGroupPostIds superGroupPost : distinct(members.superGroupPosts())) {
      superGroupPosts.add(
          new AuthoritySuperGroupPost(
              getSuperGroup(superGroupPost.superGroupId()),
              this.postRepository
                  .get(new PostId(superGroupPost.postId()))
                  .orElseThrow(PostNotFoundException::new)));
    }

    List<GammaUser> users = new ArrayList<>();
    for (UUID userId : distinct(members.userIds())) {
      users.add(
          this.userRepository.get(new UserId(userId)).orElseThrow(UserNotFoundException::new));
    }

    this.clientAuthorityRepository.save(
        authority
            .withSuperGroups(superGroups)
            .withSuperGroupPosts(superGroupPosts)
            .withUsers(users));
  }

  private SuperGroup getSuperGroup(UUID superGroupId) throws SuperGroupNotFoundException {
    return this.superGroupRepository
        .get(new SuperGroupId(superGroupId))
        .orElseThrow(SuperGroupNotFoundException::new);
  }

  private static <T> List<T> distinct(List<T> values) {
    return values.stream().filter(Objects::nonNull).distinct().toList();
  }

  public List<ClientAuthorityDTO> getAll(UUID clientUid) {
    ClientUid uid = new ClientUid(clientUid);

    this.accessGuard.requireEither(isAdmin(), ownerOfClient(uid));

    var authorities = this.clientAuthorityRepository.getAllByClient(uid);

    return authorities.stream().map(ClientAuthorityDTO::new).toList();
  }

  public List<String> getClientAuthorities() {
    this.accessGuard.require(isClientApi());

    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthentication) {
      Client client = apiAuthentication.getClient().orElseThrow();

      return this.clientAuthorityRepository.getAllByClient(client.clientUid()).stream()
          .map(Authority::name)
          .map(AuthorityName::value)
          .toList();
    }

    throw new RuntimeException();
  }

  public List<String> getUserAuthorities(UUID userId) {
    this.accessGuard.require(isClientApi());

    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthentication) {
      Client client = apiAuthentication.getClient().orElseThrow();

      return this.clientAuthorityRepository
          .getAllByUser(client.clientUid(), new UserId(userId))
          .stream()
          .map(AuthorityName::value)
          .toList();
    }

    throw new RuntimeException();
  }

  public Optional<ClientAuthorityDTO> getClientAuthority(String name) {
    this.accessGuard.require(isClientApi());

    if (AuthenticationExtractor.getAuthentication()
        instanceof ApiAuthentication apiAuthentication) {
      Client client = apiAuthentication.getClient().orElseThrow();
      AuthorityName authorityName;

      try {
        authorityName = new AuthorityName(name);
      } catch (IllegalArgumentException e) {
        // A name that fails validation can never have been created.
        return Optional.empty();
      }

      return this.clientAuthorityRepository
          .get(client.clientUid(), authorityName)
          .map(ClientAuthorityDTO::new);
    }

    throw new RuntimeException();
  }

  public record ClientAuthorityDTO(
      UUID clientUid,
      String authorityName,
      List<SuperGroupFacade.SuperGroupDTO> superGroups,
      List<SuperGroupPostDTO> superGroupPosts,
      List<UserFacade.UserDTO> users) {

    public ClientAuthorityDTO(Authority authority) {
      this(
          authority.client().clientUid().value(),
          authority.name().value(),
          authority.superGroups().stream().map(SuperGroupFacade.SuperGroupDTO::new).toList(),
          authority.superGroupPosts().stream().map(SuperGroupPostDTO::new).toList(),
          authority.users().stream().map(UserFacade.UserDTO::new).toList());
    }
  }

  public record SuperGroupPostDTO(
      SuperGroupFacade.SuperGroupDTO superGroup, PostFacade.PostDTO post) {

    public SuperGroupPostDTO(AuthoritySuperGroupPost superGroupPost) {
      this(
          new SuperGroupFacade.SuperGroupDTO(superGroupPost.superGroup()),
          new PostFacade.PostDTO(superGroupPost.post()));
    }
  }

  /** The ids of who should have an authority, see {@link #setMembers}. */
  public record AuthorityMembers(
      List<UUID> superGroupIds, List<SuperGroupPostIds> superGroupPosts, List<UUID> userIds) {

    public AuthorityMembers {
      superGroupIds = superGroupIds == null ? List.of() : superGroupIds;
      superGroupPosts = superGroupPosts == null ? List.of() : superGroupPosts;
      userIds = userIds == null ? List.of() : userIds;
    }
  }

  public record SuperGroupPostIds(UUID superGroupId, UUID postId) {}

  public static class ClientAuthorityNotFoundException extends Exception {}

  public static class SuperGroupNotFoundException extends Exception {}

  public static class PostNotFoundException extends Exception {}

  public static class UserNotFoundException extends Exception {}
}
