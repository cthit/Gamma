package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.app.oauth2.GammaRegisteredClientRepository.IS_OFFICIAL;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConsentController {

  private final RegisteredClientRepository registeredClientRepository;
  private final ClientFacade clientFacade;

  public ConsentController(
      RegisteredClientRepository registeredClientRepository, ClientFacade clientFacade) {
    this.registeredClientRepository = registeredClientRepository;
    this.clientFacade = clientFacade;
  }

  public record UserOwner(String name) {}

  public ModelAndView createClientIssuesModelAndView(
      boolean htmxRequest, String title, String description) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/client-authorizing-issue");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/client-authorizing-issue");
    }

    mv.addObject("title", title);
    mv.addObject("description", description);

    return mv;
  }

  @GetMapping("/oauth2/consent")
  public ModelAndView getOAuth2Consent(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = OAuth2ParameterNames.CLIENT_ID, required = false) String clientId,
      @RequestParam(value = OAuth2ParameterNames.SCOPE, required = false) String scope,
      @RequestParam(value = OAuth2ParameterNames.STATE, required = false) String state) {

    if (clientId == null) {
      return createClientIssuesModelAndView(
          htmxRequest, "Client id missing", "A client_id must be provided to authorize");
    }

    if (scope == null) {
      return createClientIssuesModelAndView(
          htmxRequest, "Client scopes missing", "A scope must be specified to authorize.");
    }

    if (state == null) {
      return createClientIssuesModelAndView(
          htmxRequest, "Client state missing", "A state must be specified to authorize.");
    }

    RegisteredClient client = this.registeredClientRepository.findByClientId(clientId);
    ModelAndView mv = new ModelAndView();

    if (client == null) {
      return createClientIssuesModelAndView(
          htmxRequest, "Client not found", "A client with the given client id was not found.");
    }

    List<String> scopesList = Arrays.stream(scope.split(" ")).sorted().toList();
    List<String> clientScopesOrdered = client.getScopes().stream().sorted().toList();

    if (!scopesList.equals(clientScopesOrdered)) {
      return createClientIssuesModelAndView(
          htmxRequest,
          "Mismatch scopes for client",
          "There is a mismatch between registered client scopes, and the scopes specified for this authorization.");
    }

    if (htmxRequest) {
      mv.setViewName("pages/authorize");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/authorize");
    }

    mv.addObject("clientId", clientId);
    mv.addObject("clientName", client.getClientName());
    mv.addObject("state", state);
    mv.addObject("hasEmailScope", scope.contains("email"));
    mv.addObject("scopes", scope.split(" "));

    boolean isOfficial = client.getClientSettings().getSetting(IS_OFFICIAL);

    if (!isOfficial) {
      UserFacade.UserDTO owner =
          this.clientFacade.getClientOwner(client.getClientId()).orElseThrow();

      mv.addObject(
          "userOwner",
          new UserOwner("%s '%s' %s".formatted(owner.firstName(), owner.nick(), owner.lastName())));
    }

    return mv;
  }
}
