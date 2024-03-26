package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.app.oauth2.GammaRegisteredClientRepository.IS_OFFICIAL;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.user.UserFacade;
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

  @GetMapping("/oauth2/consent")
  public ModelAndView getOAuth2Consent(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
      @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
      @RequestParam(OAuth2ParameterNames.STATE) String state) {

    RegisteredClient client = this.registeredClientRepository.findByClientId(clientId);

    if (client == null) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/consent");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/consent");
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
