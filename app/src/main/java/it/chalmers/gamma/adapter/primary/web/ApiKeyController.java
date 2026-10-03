package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.app.apikey.ApiKeySettingsFacade;
import it.chalmers.gamma.app.apikey.SuperGroupTypeRestrictions;
import it.chalmers.gamma.app.apikey.domain.Scope;
import it.chalmers.gamma.app.common.PrettyName.PrettyNameValidator;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApiKeyController {

  private final ApiKeyFacade apiKeyFacade;
  private final ApiKeySettingsFacade apiKeySettingsFacade;
  private final SuperGroupFacade superGroupFacade;

  public ApiKeyController(
      ApiKeyFacade apiKeyFacade,
      ApiKeySettingsFacade apiKeySettingsFacade,
      SuperGroupFacade superGroupFacade) {
    this.apiKeyFacade = apiKeyFacade;
    this.apiKeySettingsFacade = apiKeySettingsFacade;
    this.superGroupFacade = superGroupFacade;
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

  private ModelAndView createGetApiKey(
      boolean htmxRequest, String apiKeyId, @Nullable String token) {
    if (!isValidUUID(apiKeyId)) {
      return createApiKeyNotFound(apiKeyId, htmxRequest);
    }

    Optional<ApiKeyFacade.ApiKeyDTO> maybeApiKey =
        this.apiKeyFacade.getById(UUID.fromString(apiKeyId));

    if (maybeApiKey.isEmpty()) {
      return createApiKeyNotFound(apiKeyId, htmxRequest);
    }

    ApiKeyFacade.ApiKeyDTO apiKey = maybeApiKey.get();
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("api-key-details/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "api-key-details/page");
    }

    mv.addObject("apiKey", apiKey);
    mv.addObject("apiKeyId", apiKey.id());
    mv.addObject(
        "sensitiveScopes",
        this.apiKeyFacade.getAllScopes().stream()
            .filter(ApiKeyFacade.ScopeInfo::sensitive)
            .map(ApiKeyFacade.ScopeInfo::scope)
            .toList());

    boolean hasAccountsProvision = apiKey.scopes().contains(Scope.ACCOUNTS_PROVISION);
    if (apiKey.scopes().stream().anyMatch(SuperGroupTypeRestrictions.RESTRICTED_SCOPES::contains)) {
      loadApiKeyScopeSettings(mv, apiKey.id(), hasAccountsProvision);
    }

    if (token != null) {
      mv.addObject("apiKeyToken", token);
    }

    return mv;
  }

  private void loadApiKeyScopeSettings(ModelAndView mv, UUID apiKeyId, boolean showGdprFilter) {
    var configs = this.apiKeySettingsFacade.getUnifiedSettings(apiKeyId);
    mv.addObject("showGdprFilter", showGdprFilter);
    mv.addObject(
        "form",
        new UnifiedSettingsForm(
            configs.stream().map(c -> new UnifiedTypeConfig(c.type(), c.gdprFilter())).toList()));
    mv.addObject(
        "allSuperGroupTypes",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());
  }

  private ModelAndView createApiKeyNotFound(String apiKeyId, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("api-key-details/not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/api-key-not-found");
    }

    mv.addObject("id", apiKeyId);

    return mv;
  }

  @GetMapping("/api-keys/{id}")
  public ModelAndView getApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String apiKeyId) {
    return createGetApiKey(htmxRequest, apiKeyId, null);
  }

  public record CreateApiKey(
      @ValidatedWith(PrettyNameValidator.class) String prettyName,
      String svDescription,
      String enDescription,
      String bundle,
      List<String> scopes) {
    public CreateApiKey() {
      this("", "", "", "INFO", List.of());
    }
  }

  public ModelAndView createGetCreateApiKey(
      boolean htmxRequest, CreateApiKey form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("create-api-key/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "create-api-key/page");
    }

    if (form == null) {
      form = new CreateApiKey();
    }

    mv.addObject("form", form);
    mv.addObject("allScopes", this.apiKeyFacade.getDataScopes());

    var bundleScopeMap = new LinkedHashMap<String, String>();
    for (var bundle : this.apiKeyFacade.getScopeBundles()) {
      bundleScopeMap.put(bundle.name(), String.join("\n", bundle.scopes()));
    }
    mv.addObject("bundleScopeMap", bundleScopeMap);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/api-keys/create")
  public ModelAndView getCreateApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetCreateApiKey(htmxRequest, null, null);
  }

  @PostMapping("/api-keys/create")
  public ModelAndView createApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateApiKey form,
      BindingResult bindingResult,
      HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return createGetCreateApiKey(htmxRequest, form, bindingResult);
    }

    ApiKeyFacade.CreatedApiKey createdApiKey;
    try {
      Set<Scope> scopes = ApiKeyFacade.resolveScopes(form.bundle, form.scopes);
      createdApiKey =
          this.apiKeyFacade.create(
              new ApiKeyFacade.NewApiKey(
                  form.prettyName,
                  form.svDescription,
                  form.enDescription,
                  scopes.stream().map(Scope::name).toList()));
    } catch (IllegalArgumentException e) {
      ModelAndView errorView = createGetCreateApiKey(htmxRequest, form, null);
      errorView.addObject("errorMessage", e.getMessage());
      return errorView;
    }

    String apiKeyId = createdApiKey.apiKey().id().toString();

    response.addHeader("HX-Push-Url", "/api-keys/" + apiKeyId);

    return createGetApiKey(htmxRequest, apiKeyId, createdApiKey.token());
  }

  @DeleteMapping("/api-keys/{id}")
  public ModelAndView deleteApiKey(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      HttpServletResponse response,
      @PathVariable("id") UUID id) {
    try {
      this.apiKeyFacade.delete(id);
    } catch (ApiKeyFacade.ApiKeyNotFoundException e) {
      throw new RuntimeException(e);
    }

    response.addHeader("HX-Redirect", "/api-keys");

    return new ModelAndView("common/empty");
  }

  public static final class UnifiedTypeConfig {
    public String type;
    public boolean gdprFilter;

    public UnifiedTypeConfig() {}

    public UnifiedTypeConfig(String type, boolean gdprFilter) {
      this.type = type;
      this.gdprFilter = gdprFilter;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public boolean isGdprFilter() {
      return gdprFilter;
    }

    public void setGdprFilter(boolean gdprFilter) {
      this.gdprFilter = gdprFilter;
    }
  }

  public static final class UnifiedSettingsForm {
    public List<UnifiedTypeConfig> superGroupTypes = new ArrayList<>();

    public UnifiedSettingsForm() {}

    public UnifiedSettingsForm(List<UnifiedTypeConfig> superGroupTypes) {
      this.superGroupTypes = superGroupTypes;
    }

    public List<UnifiedTypeConfig> getSuperGroupTypes() {
      return superGroupTypes;
    }

    public void setSuperGroupTypes(List<UnifiedTypeConfig> superGroupTypes) {
      this.superGroupTypes = superGroupTypes;
    }
  }

  @PutMapping("/api-keys/{apiKeyId}/unified-settings")
  public ModelAndView updateUnifiedSettings(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("apiKeyId") UUID apiKeyId,
      UnifiedSettingsForm form) {

    ApiKeyFacade.ApiKeyDTO apiKey =
        this.apiKeyFacade
            .getById(apiKeyId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Api key not found"));

    Set<String> validTypes = new HashSet<>(this.superGroupFacade.getAllTypes());
    Set<String> seenTypes = new HashSet<>();
    for (UnifiedTypeConfig typeConfig : form.superGroupTypes) {
      if (!validTypes.contains(typeConfig.type)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Unknown super group type: " + typeConfig.type);
      }
      if (!seenTypes.add(typeConfig.type)) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Duplicate super group type: " + typeConfig.type);
      }
    }

    this.apiKeySettingsFacade.setUnifiedSettings(
        apiKeyId,
        form.superGroupTypes.stream()
            .map(c -> new ApiKeySettingsFacade.SuperGroupTypeConfigDTO(c.type, c.gdprFilter))
            .toList());

    ModelAndView mv = new ModelAndView("api-key-details/unified-settings");

    mv.addObject("apiKeyId", apiKeyId);
    boolean hasAccountsProvision = apiKey.scopes().contains(Scope.ACCOUNTS_PROVISION);
    mv.addObject("showGdprFilter", hasAccountsProvision);
    mv.addObject("form", form);

    return mv;
  }

  @GetMapping("/api-keys/{apiKeyId}/new-super-group-type/unified")
  public ModelAndView getNewSuperGroupTypeUnified(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("apiKeyId") UUID apiKeyId) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("api-key-details/new-type-to-unified-settings");
    var apiKey =
        this.apiKeyFacade
            .getById(apiKeyId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Api key not found"));
    mv.addObject("showGdprFilter", apiKey.scopes().contains(Scope.ACCOUNTS_PROVISION));
    mv.addObject(
        "superGroupTypes",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());

    return mv;
  }

  @PostMapping("/api-keys/{apiKeyId}/reset")
  public ModelAndView resetApiKeyToken(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("apiKeyId") UUID apiKeyId) {
    String newToken = this.apiKeyFacade.resetApiKey(apiKeyId);

    return createGetApiKey(htmxRequest, apiKeyId.toString(), newToken);
  }
}
