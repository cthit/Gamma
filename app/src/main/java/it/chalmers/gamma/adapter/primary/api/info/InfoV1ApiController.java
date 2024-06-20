package it.chalmers.gamma.adapter.primary.api.info;

import it.chalmers.gamma.adapter.primary.api.NotFoundResponse;
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
 * info are going to be returned, and that should be used with caution.
 */
@RestController
@RequestMapping(InfoV1ApiController.URI)
public class InfoV1ApiController {

  public static final String URI = "/api/info/v1";

  private final SuperGroupFacade superGroupFacade;
  private final UserFacade userFacade;

  public InfoV1ApiController(SuperGroupFacade superGroupFacade, UserFacade userFacade) {
    this.superGroupFacade = superGroupFacade;
    this.userFacade = userFacade;
  }

  @GetMapping("/users/{id}")
  public UserFacade.UserWithGroupsDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.getWithGroups(id).orElseThrow(UserNotFoundResponse::new);
  }

  @GetMapping("/blob")
  public List<SuperGroupFacade.SuperGroupTypeDTO> getGroups() {
    return this.superGroupFacade.getAllTypesWithSuperGroups();
  }

  private static class UserNotFoundResponse extends NotFoundResponse {}
}
