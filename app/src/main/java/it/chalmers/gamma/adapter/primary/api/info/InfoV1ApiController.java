package it.chalmers.gamma.adapter.primary.api.info;

import it.chalmers.gamma.adapter.primary.api.utils.NotFoundResponse;
import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.settings.SettingsFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
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

  private final SuperGroupFacade superGroupFacade;
  private final UserFacade userFacade;
  private final SettingsFacade settingsFacade;

  public InfoV1ApiController(
      SuperGroupFacade superGroupFacade, UserFacade userFacade, SettingsFacade settingsFacade) {
    this.superGroupFacade = superGroupFacade;
    this.userFacade = userFacade;
    this.settingsFacade = settingsFacade;
  }

  @GetMapping("/users/{id}")
  public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.get(id).orElseThrow(UserNotFoundResponse::new);
  }

  @GetMapping("/blob")
  public List<SuperGroupFacade.SuperGroupTypeDTO> getGroups() {
    return this.superGroupFacade.getAllTypesWithSuperGroups(
        this.settingsFacade.getInfoApiSuperGroupTypes());
  }

  public record GroupsResponse(List<GroupFacade.GroupWithMembersDTO> groups) {}

  private static class UserNotFoundResponse extends NotFoundResponse {}
}
