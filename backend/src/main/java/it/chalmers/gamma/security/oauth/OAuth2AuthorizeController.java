package it.chalmers.gamma.security.oauth;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.client.domain.ClientId;
import it.chalmers.gamma.domain.client.data.dto.ClientDTO;
import it.chalmers.gamma.domain.client.service.ClientFinder;

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

    private final ClientFinder clientFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthorizeController.class);

    public OAuth2AuthorizeController(ClientFinder clientFinder) {
        this.clientFinder = clientFinder;
    }

    @GetMapping("/oauth/confirm_access")
    public String getConfirmAccess(@ModelAttribute AuthorizationRequest clientAuth, Model model) {
        try {
            ClientDTO client = this.clientFinder.get(new ClientId(clientAuth.getClientId()));
            model.addAttribute("clientName", client.getName());
        } catch (EntityNotFoundException e) {
            LOGGER.error("Cannot find provided client in authorize", e);
        }

        return "authorize";
    }

}

