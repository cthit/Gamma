package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ApiKeyController {

    private final ApiKeyFacade apiKeyFacade;

    public ApiKeyController(ApiKeyFacade apiKeyFacade) {
        this.apiKeyFacade = apiKeyFacade;
    }

    @GetMapping("/api-keys")
    public ModelAndView getApiKeys(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<ApiKeyFacade.ApiKeyDTO> apiKeys = this.apiKeyFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/api-keys");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/api-keys");
        }

        mv.addObject("apiKeys", apiKeys);

        return mv;
    }

    @GetMapping("/api-keys/{id}")
    public ModelAndView getApiKey(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<ApiKeyFacade.ApiKeyDTO> apiKey = this.apiKeyFacade.getById(id);

        if(apiKey.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/api-key-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/api-key-details");
        }
        mv.addObject("apiKey", apiKey.get());

        return mv;
    }

}
