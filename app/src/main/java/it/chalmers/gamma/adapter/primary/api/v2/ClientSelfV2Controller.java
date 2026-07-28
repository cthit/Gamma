package it.chalmers.gamma.adapter.primary.api.v2;

import it.chalmers.gamma.app.client.ClientAuthorityFacade;
import it.chalmers.gamma.app.client.domain.Client;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.security.authentication.ApiAuthentication;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/clients/self")
public class ClientSelfV2Controller {

  private final UserFacade userFacade;
  private final ClientAuthorityFacade clientAuthorityFacade;

  public ClientSelfV2Controller(
      UserFacade userFacade, ClientAuthorityFacade clientAuthorityFacade) {
    this.userFacade = userFacade;
    this.clientAuthorityFacade = clientAuthorityFacade;
  }

  private Client getCurrentClient() {
    if (AuthenticationExtractor.getAuthentication() instanceof ApiAuthentication apiAuth) {
      return apiAuth
          .getClient()
          .orElseThrow(() -> new RuntimeException("API key is not linked to a client"));
    }
    throw new RuntimeException("Not an API authentication");
  }

  @GetMapping("/users")
  public List<UserFacade.UserDTO> getUsers() {
    Client client = getCurrentClient();
    return this.userFacade.fetchUsersByClientApproval(client.clientUid());
  }

  @GetMapping("/users/{id}")
  public UserFacade.UserDTO getUser(@PathVariable("id") UUID id) {
    return this.userFacade.fetchUser(id).orElseThrow(V2NotFoundResponse::new);
  }

  @GetMapping("/users/{id}/groups")
  public List<UserFacade.UserGroupDTO> getUserGroups(@PathVariable("id") UUID id) {
    UserFacade.UserWithGroupsDTO user =
        this.userFacade.fetchUserWithGroups(id).orElseThrow(V2NotFoundResponse::new);
    return user.groups();
  }

  @GetMapping("/authorities")
  public List<String> getAuthorities() {
    return this.clientAuthorityFacade.fetchClientAuthorities();
  }

  @GetMapping("/authorities/for/{id}")
  public List<String> getAuthoritiesForUser(@PathVariable("id") UUID userId) {
    return this.clientAuthorityFacade.fetchUserAuthorities(userId);
  }
}
