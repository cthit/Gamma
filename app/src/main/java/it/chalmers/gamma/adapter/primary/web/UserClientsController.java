package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserClientsController {

  private final ClientFacade clientFacade;
  private final UserFacade userFacade;

  public UserClientsController(ClientFacade clientFacade, UserFacade userFacade) {
    this.clientFacade = clientFacade;
    this.userFacade = userFacade;
  }

  public record UserClient(
      String prettyName,
      String clientId,
      String fullName,
      String email,
      UUID userId,
      UUID clientUid) {
    public UserClient(ClientFacade.ClientDTO client, UserFacade.UserDTO user, String email) {
      this(
          client.prettyName(),
          client.clientId(),
          user.fullName(),
          email,
          user.id(),
          client.clientUid());
    }

    public String mailto() {
      return email == null ? null : MailtoLink.href(email);
    }
  }

  @GetMapping("/user-clients")
  public ModelAndView getUserClients(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/user-clients");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/user-clients");
    }

    mv.addObject(
        "clients",
        this.clientFacade.getAll().stream()
            .filter(client -> client.owner() instanceof ClientFacade.ClientDTO.UserOwner)
            .map(
                client -> {
                  UserFacade.UserDTO user =
                      ((ClientFacade.ClientDTO.UserOwner) client.owner()).user();
                  String email =
                      this.userFacade
                          .getExtendedAsAdmin(user.id())
                          .map(UserFacade.UserExtendedDTO::email)
                          .orElse(null);
                  return new UserClient(client, user, email);
                })
            .toList());

    return mv;
  }
}
