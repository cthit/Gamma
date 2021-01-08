package it.chalmers.gamma.oauth;

import it.chalmers.gamma.domain.access.ITClientDTO;
import it.chalmers.gamma.client.ITClientService;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class OAuth2AuthorizeController {

    private final ITClientService clientService;

    public OAuth2AuthorizeController(ITClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/oauth/confirm_access")
    public String getConfirmAccess(@ModelAttribute AuthorizationRequest clientAuth, Model model) {
        ITClientDTO client = this.clientService.getITClientById(clientAuth.getClientId()).orElseThrow();
        model.addAttribute("clientName", client.getName());
        return "authorize";
    }

}

