package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class UserClientsController {

    private final ClientFacade clientFacade;

    public UserClientsController(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    public record UserClient(String prettyName, String ownerUserName, UUID clientUid) {
        public UserClient(ClientFacade.ClientDTO client, UserFacade.UserDTO user) {
            this(client.prettyName(), user.firstName() + " " + user.lastName(), client.clientUid());
        }
    }

    @GetMapping("/user-clients")
    public ModelAndView getUserClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/user-clients");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/user-clients");
        }

        mv.addObject("clients", this.clientFacade.getAll()
                .stream()
                .filter(client -> client.owner() instanceof ClientFacade.ClientDTO.UserOwner)
                .map(client -> new UserClient(client, ((ClientFacade.ClientDTO.UserOwner) client.owner()).user()))
                .toList()
        );

        return mv;
    }

}
