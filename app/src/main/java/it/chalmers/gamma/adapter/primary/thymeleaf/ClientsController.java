package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.client.ClientFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ClientsController {

    private final ClientFacade clientFacade;

    public ClientsController(ClientFacade clientFacade) {
        this.clientFacade = clientFacade;
    }

    @GetMapping("/clients")
    public ModelAndView getClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<ClientFacade.ClientDTO> clients = this.clientFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/clients");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/clients");
        }
        mv.addObject("clients", clients);

        return mv;
    }

    @GetMapping("/clients/{id}")
    public ModelAndView getClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID clientUid) {
        Optional<ClientFacade.ClientDTO> client = this.clientFacade.get(clientUid);

        if(client.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/client-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/client-details");
        }
        mv.addObject("client", client.get());

        return mv;
    }


}
