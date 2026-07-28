package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.post.PostFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/users")
public class UserV2Controller {

  private final UserFacade userFacade;
  private final GroupFacade groupFacade;

  public UserV2Controller(UserFacade userFacade, GroupFacade groupFacade) {
    this.userFacade = userFacade;
    this.groupFacade = groupFacade;
  }

  @GetMapping
  public List<UserFacade.UserDTO> getUsers() {
    return this.userFacade.fetchAllUsers();
  }

  @GetMapping("/{id}")
  public UserFacade.UserDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.fetchUser(id).orElseThrow(V2NotFoundResponse::new);
  }

  @GetMapping("/{id}/groups")
  public List<V2UserGroup> getUserGroups(@PathVariable("id") UUID id) {
    UserFacade.UserWithGroupsDTO user = this.userFacade.fetchUserWithGroups(id)
        .orElseThrow(V2NotFoundResponse::new);
    return user.groups().stream().map(V2UserGroup::new).toList();
  }

  public record V2UserGroup(
      UUID groupId,
      String groupName,
      String groupPrettyName,
      SuperGroupFacade.SuperGroupDTO superGroup,
      PostFacade.PostDTO post) {
    public V2UserGroup(UserFacade.UserGroupDTO ug) {
      this(ug.group().id(), ug.group().name(), ug.group().prettyName(), ug.group().superGroup(), ug.post());
    }
  }
}
