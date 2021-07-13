package it.chalmers.gamma.security.oauth;

import it.chalmers.gamma.app.domain.ClientId;
import it.chalmers.gamma.app.domain.Client;

import it.chalmers.gamma.app.client.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class OAuth2AuthorizeController {

    private final ClientService clientService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizeController.class);

    public OAuth2AuthorizeController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/oauth/confirm_access")
    public String getConfirmAccess(@ModelAttribute AuthorizationRequest clientAuth, Model model) {
        try {
            Client client = this.clientService.get(ClientId.valueOf(clientAuth.getClientId())).client();
            model.addAttribute("clientName", client.prettyName());
        } catch (ClientService.ClientNotFoundException e) {
            LOGGER.error("Cannot find provided client in authorize", e);
        }

        return "authorize";
    }

}

