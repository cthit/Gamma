package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
public class ConsentController {

    private final RegisteredClientRepository registeredClientRepository;

    public ConsentController(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    @GetMapping("/oauth2/consent")
    public ModelAndView getOAuth2Consent(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
                                         @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
                                         @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
                                         @RequestParam(OAuth2ParameterNames.STATE) String state) {
        
        RegisteredClient client = this.registeredClientRepository.findByClientId(clientId);

        //TODO: Do something better than this.
        if (client == null) {
            return null;
        }

        ModelAndView mv = new ModelAndView();
        
        if(htmxRequest) {
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


        return mv;
    }

}
