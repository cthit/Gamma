package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApiKeyController {

  private final ApiKeyFacade apiKeyFacade;

  public ApiKeyController(ApiKeyFacade apiKeyFacade) {
    this.apiKeyFacade = apiKeyFacade;
  }

  @GetMapping("/api-keys")
  public ModelAndView getApiKeys(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<ApiKeyFacade.ApiKeyDTO> apiKeys = this.apiKeyFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/api-keys");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/api-keys");
    }

    mv.addObject("apiKeys", apiKeys);

    return mv;
  }

  @GetMapping("/api-keys/{id}")
  public ModelAndView getApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    Optional<ApiKeyFacade.ApiKeyDTO> apiKey = this.apiKeyFacade.getById(id);

    if (apiKey.isEmpty()) {
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

  public record CreateApiKey(
      String prettyName, String svDescription, String enDescription, String keyType) {}

  @GetMapping("/api-keys/create")
  public ModelAndView getCreateApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/create-api-key");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/create-api-key");
    }

    mv.addObject("form", new CreateApiKey("", "", "", ""));
    mv.addObject("keyTypes", this.apiKeyFacade.getApiKeyTypes());

    return mv;
  }

  @PostMapping("/api-keys")
  public ModelAndView createApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateApiKey form,
      BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    ApiKeyFacade.CreatedApiKey apiKeyCredentials =
        this.apiKeyFacade.create(
            new ApiKeyFacade.NewApiKey(
                form.prettyName, form.svDescription, form.enDescription, form.keyType));

    mv.setViewName("pages/api-key-credentials");
    mv.addObject("apiKeyId", apiKeyCredentials.apiKeyId());
    mv.addObject("apiKeyToken", apiKeyCredentials.token());
    mv.addObject("name", form.prettyName);

    return mv;
  }

  @DeleteMapping("/api-keys/{id}")
  public ModelAndView deleteApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    try {
      this.apiKeyFacade.delete(id);
    } catch (ApiKeyFacade.ApiKeyNotFoundException e) {
      throw new RuntimeException(e);
    }

    return new ModelAndView("redirect:/api-keys");
  }
}
