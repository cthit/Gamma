package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.accountscaffold.AccountScaffoldFacade;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/provision")
public class ProvisionV2Controller {

  private final AccountScaffoldFacade accountScaffoldFacade;

  public ProvisionV2Controller(AccountScaffoldFacade accountScaffoldFacade) {
    this.accountScaffoldFacade = accountScaffoldFacade;
  }

  @GetMapping("/super-groups")
  public List<AccountScaffoldFacade.AccountScaffoldSuperGroupDTO> getProvisionSuperGroups() {
    return this.accountScaffoldFacade.fetchActiveSuperGroups();
  }

  @GetMapping("/users")
  public List<AccountScaffoldFacade.AccountScaffoldUserDTO> getProvisionUsers() {
    return this.accountScaffoldFacade.fetchActiveUsers();
  }
}
