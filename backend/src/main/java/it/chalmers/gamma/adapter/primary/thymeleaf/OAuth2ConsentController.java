package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class OAuth2ConsentController {

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2ConsentController(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @GetMapping("/oauth2/consent")
    public String getOAuth2Consent(Principal principal, Model model,
                                   @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                   @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                   @RequestParam(OAuth2ParameterNames.STATE) String state) {

        RegisteredClient client = this.registeredClientRepository.findByClientId(clientId);

        //TODO: Do something better than this.
        if (client == null) {
            return null;
        }

        model.addAttribute("clientId", clientId);
        model.addAttribute("clientName", client.getClientName());
        model.addAttribute("state", state);
        model.addAttribute("hasEmailScope", scope.contains("email"));
        model.addAttribute("scopes", scope.split(" "));


        return "consent";
    }

}
