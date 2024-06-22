package it.chalmers.gamma.adapter.primary.api.client;

import it.chalmers.gamma.app.authentication.AccessGuard;
import it.chalmers.gamma.app.client.ClientAuthorityFacade;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ClientApiV1Controller.URI)
public class ClientApiV1Controller {

  public static final String URI = "/api/client/v1";

  private final UserFacade userFacade;
  private final GroupFacade groupFacade;
  private final SuperGroupFacade superGroupFacade;
  private final ClientAuthorityFacade clientAuthorityFacade;

  public ClientApiV1Controller(
      UserFacade userFacade,
      GroupFacade groupFacade,
      SuperGroupFacade superGroupFacade,
      ClientAuthorityFacade clientAuthorityFacade) {
    this.userFacade = userFacade;
    this.groupFacade = groupFacade;
    this.superGroupFacade = superGroupFacade;
    this.clientAuthorityFacade = clientAuthorityFacade;
  }

  record ClientV1SuperGroup(
      UUID id,
      String name,
      String prettyName,
      String type,
      String svDescription,
      String enDescription) {
    ClientV1SuperGroup(SuperGroupFacade.SuperGroupDTO superGroup) {
      this(
          superGroup.id(),
          superGroup.name(),
          superGroup.prettyName(),
          superGroup.type(),
          superGroup.svDescription(),
          superGroup.enDescription());
    }
  }

  record ClientV1Post(UUID id, int version, String svName, String enName) {
    public ClientV1Post(PostFacade.PostDTO post) {
      this(post.id(), post.version(), post.svName(), post.enName());
    }
  }

  record ClientV1UserGroup(
      UUID id, String name, String prettyName, ClientV1SuperGroup superGroup, ClientV1Post post) {
    ClientV1UserGroup(UserFacade.UserGroupDTO userGroup) {
      this(
          userGroup.group().id(),
          userGroup.group().name(),
          userGroup.group().prettyName(),
          new ClientV1SuperGroup(userGroup.group().superGroup()),
          new ClientV1Post(userGroup.post()));
    }
  }

  record ClientV1User(
      String cid, String nick, String firstName, String lastName, UUID id, int acceptanceYear) {
    ClientV1User(UserFacade.UserDTO user) {
      this(
          user.cid(),
          user.nick(),
          user.firstName(),
          user.lastName(),
          user.id(),
          user.acceptanceYear());
    }
  }

  record ClientV1Group(UUID id, String name, String prettyName, ClientV1SuperGroup superGroup) {
    ClientV1Group(GroupFacade.GroupDTO group) {
      this(
          group.id(), group.name(), group.prettyName(), new ClientV1SuperGroup(group.superGroup()));
    }
  }

  @GetMapping("/groups")
  List<ClientV1Group> getGroups() {
    return this.groupFacade.getAll().stream().map(ClientV1Group::new).toList();
  }

  @GetMapping("/superGroups")
  List<ClientV1SuperGroup> getSuperGroups() {
    return this.superGroupFacade.getAll().stream().map(ClientV1SuperGroup::new).toList();
  }

  @GetMapping("/users")
  List<ClientV1User> getUsersForClient() {
    return this.userFacade.getAllByClientAccepting().stream().map(ClientV1User::new).toList();
  }

  @GetMapping("/users/{id}")
  ClientV1User getUser(@PathVariable("id") UUID id) {
    Optional<UserFacade.UserDTO> maybeUser;

    try {
      maybeUser = this.userFacade.get(id);
    } catch (AccessGuard.AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found Or Unauthorized");
    }

    if (maybeUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found Or Unauthorized");
    }

    return maybeUser.map(ClientV1User::new).get();
  }

  @GetMapping("/groups/for/{id}")
  List<ClientV1UserGroup> getGroupsForUser(@PathVariable("id") UUID id) {
    Optional<UserFacade.UserWithGroupsDTO> maybeUser;

    try {
      maybeUser = this.userFacade.getWithGroups(id);
    } catch (AccessGuard.AccessDeniedException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found Or Unauthorized");
    }

    if (maybeUser.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found Or Unauthorized");
    }

    List<UserFacade.UserGroupDTO> groups = maybeUser.get().groups();

    return groups.stream().map(ClientV1UserGroup::new).toList();
  }

  @GetMapping("/authorities")
  public List<String> getClientAuthorities() {
    return this.clientAuthorityFacade.getClientAuthorities();
  }

  @GetMapping("/authorities/for/{id}")
  public List<String> getClientAuthoritiesForUser(@PathVariable("id") UUID userId) {
    return this.clientAuthorityFacade.getUserAuthorities(userId);
  }
}
