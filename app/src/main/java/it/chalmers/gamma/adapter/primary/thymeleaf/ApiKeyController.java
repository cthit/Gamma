package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ApiKeyController {

    private final ApiKeyFacade apiKeyFacade;

    public ApiKeyController(ApiKeyFacade apiKeyFacade) {
        this.apiKeyFacade = apiKeyFacade;
    }

    @GetMapping("/api-keys")
    public ModelAndView getApiKeys(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<ApiKeyFacade.ApiKeyDTO> apiKeys = this.apiKeyFacade.getAll();

        ModelAndView mv = Page.SHOW_API_KEYS.create(htmxRequest);
        mv.addObject("apiKeys", apiKeys);

        return mv;
    }

}