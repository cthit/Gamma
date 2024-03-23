package it.chalmers.gamma.adapter.primary.api.accountscaffold;

import it.chalmers.gamma.app.accountscaffold.AccountScaffoldFacade;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Used by, for example, <a href="https://github.com/cthit/goldapps">cthit/goldapps</a> to sync
 * Google accounts for the student division.
 */
@RestController
@RequestMapping(AccountScaffoldV1ApiController.URI)
public class AccountScaffoldV1ApiController {

  public static final String URI = "/api/account-scaffold/v1";

  private final AccountScaffoldFacade accountScaffoldFacade;

  public AccountScaffoldV1ApiController(AccountScaffoldFacade accountScaffoldFacade) {
    this.accountScaffoldFacade = accountScaffoldFacade;
  }

  @GetMapping("/supergroups")
  public List<AccountScaffoldFacade.AccountScaffoldSuperGroupDTO> getSuperGroups() {
    return this.accountScaffoldFacade.getActiveSuperGroups();
  }

  @GetMapping("/users")
  public List<AccountScaffoldFacade.AccountScaffoldUserDTO> getUsers() {
    return this.accountScaffoldFacade.getActiveUsers();
  }
}
