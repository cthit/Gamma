package it.chalmers.gamma.adapter.primary.api.info;

import it.chalmers.gamma.adapter.primary.api.utils.NotFoundResponse;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Going to be used by chalmers.it to display groups and their members. A separate API since user
 * info are going to be returned, and that should be used with caution. If you need changes, then
 * create a new version of the API.
 */
@RestController
@RequestMapping(InfoV1ApiController.URI)
public class InfoV1ApiController {

  public static final String URI = "/api/info/v1";

  private final GroupFacade groupFacade;
  private final UserFacade userFacade;

  public InfoV1ApiController(GroupFacade groupFacade, UserFacade userFacade) {
    this.groupFacade = groupFacade;
    this.userFacade = userFacade;
  }

  @GetMapping("/users/{id}")
  public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.get(id).orElseThrow(UserNotFoundResponse::new);
  }

  @GetMapping("/groups")
  public GroupsResponse getGroups() {
    return new GroupsResponse(this.groupFacade.getAllForInfoApi());
  }

  record GroupsResponse(List<GroupFacade.GroupWithMembersDTO> groups) {}

  private static class UserNotFoundResponse extends NotFoundResponse {}
}
