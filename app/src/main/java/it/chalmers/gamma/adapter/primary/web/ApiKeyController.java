package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

import it.chalmers.gamma.app.apikey.ApiKeyFacade;
import it.chalmers.gamma.app.apikey.ApiKeySettingsFacade;
import it.chalmers.gamma.app.common.PrettyName.PrettyNameValidator;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    if (apiKey.keyType().equals("ACCOUNT_SCAFFOLD")) {
      loadApiKeySettingsAccountScaffold(mv, apiKey.id());
    } else if (apiKey.keyType().equals("INFO")) {
      loadApiKeySettingsInfo(mv, apiKey.id());
    }

    if (token != null) {
      mv.addObject("apiKeyToken", token);
    }

    return mv;
  }

  private void loadApiKeySettingsInfo(ModelAndView mv, UUID apiKeyId) {
    var settings = this.apiKeySettingsFacade.getInfoSettings(apiKeyId);

    mv.addObject("settings_title", "Info settings");
    mv.addObject(
        "settings_description",
        "Set the super group types from which the information will be query from");
    mv.addObject(
        "settings_form", new InfoApiKeySettings(settings.version(), settings.superGroupTypes()));
  }

  private void loadApiKeySettingsAccountScaffold(ModelAndView mv, UUID apiKeyId) {
    var settings = this.apiKeySettingsFacade.getAccountScaffoldSettings(apiKeyId);

    mv.addObject("settings_title", "Account scaffold settings");
    mv.addObject(
        "settings_description",
        "Set the super group types from which the information will query from");
    mv.addObject(
        "settings_form",
        new AccountScaffoldApiKeySettings(
            settings.version(),
            settings.superGroupTypes().stream()
                .map(
                    type ->
                        new AccountScaffoldType(type.type(), type.requiresManaged() ? "on" : "off"))
                .toList()));
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
      String keyType) {}

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
      form = new CreateApiKey("", "", "", "");
    }

    mv.addObject("form", form);
    mv.addObject("keyTypes", this.apiKeyFacade.getApiKeyTypes());

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

    ApiKeyFacade.CreatedApiKey createdApiKey =
        this.apiKeyFacade.create(
            new ApiKeyFacade.NewApiKey(
                form.prettyName, form.svDescription, form.enDescription, form.keyType));

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

  public static class AccountScaffoldType {
    public String type;
    public String requiresManaged;

    public AccountScaffoldType() {}

    public AccountScaffoldType(String type, String requiresManaged) {
      this.type = type;
      this.requiresManaged = requiresManaged;
    }

    public String getRequiresManaged() {
      return requiresManaged;
    }

    public void setRequiresManaged(String requiresManaged) {
      this.requiresManaged = requiresManaged;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static final class AccountScaffoldApiKeySettings {
    public List<AccountScaffoldType> superGroupTypes = new ArrayList<>();
    public int version;

    public AccountScaffoldApiKeySettings() {}

    public AccountScaffoldApiKeySettings(int version, List<AccountScaffoldType> types) {
      this.version = version;
      this.superGroupTypes = types;
    }

    public List<AccountScaffoldType> getSuperGroupTypes() {
      return superGroupTypes;
    }

    public void setSuperGroupTypes(List<AccountScaffoldType> superGroupTypes) {
      this.superGroupTypes = superGroupTypes;
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }
  }

  public static final class InfoApiKeySettings {
    public List<String> superGroupTypes = new ArrayList<>();
    public int version;

    public InfoApiKeySettings() {}

    public InfoApiKeySettings(int version, List<String> types) {
      this.version = version;
      this.superGroupTypes = types;
    }

    public List<String> getSuperGroupTypes() {
      return superGroupTypes;
    }

    public void setSuperGroupTypes(List<String> superGroupTypes) {
      this.superGroupTypes = superGroupTypes;
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }
  }

  @GetMapping("/api-keys/new-super-group-type/info")
  public ModelAndView getNewSuperGroupTypeInfo(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("api-key-details/new-type-to-info-settings");
    mv.addObject(
        "superGroupTypes",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());

    return mv;
  }

  @GetMapping("/api-keys/new-super-group-type/account-scaffold")
  public ModelAndView getNewSuperGroupTypeAccountScaffold(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("api-key-details/new-type-to-account-scaffold-settings");
    mv.addObject(
        "superGroupTypes",
        this.superGroupFacade.getAllTypes().stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .toList());

    return mv;
  }

  @PutMapping("/api-keys/{apiKeyId}/account-scaffold-settings")
  public ModelAndView updateAccountScaffoldSettings(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("apiKeyId") UUID apiKeyId,
      AccountScaffoldApiKeySettings form) {

    Optional<ApiKeyFacade.ApiKeyDTO> apiKeyMaybe = this.apiKeyFacade.getById(apiKeyId);

    if (apiKeyMaybe.isEmpty()) {
      throw new RuntimeException();
    }

    ApiKeyFacade.ApiKeyDTO apiKey = apiKeyMaybe.get();

    ModelAndView mv = new ModelAndView("api-key-details/account-scaffold-settings");

    this.apiKeySettingsFacade.setAccountScaffoldSettings(
        apiKeyId,
        new ApiKeySettingsFacade.ApiKeySettingsAccountScaffoldDTO(
            form.version,
            form.superGroupTypes.stream()
                .map(
                    accountScaffoldType ->
                        new ApiKeySettingsFacade.AccountScaffoldTypeDTO(
                            accountScaffoldType.type,
                            "on".equals(accountScaffoldType.requiresManaged)))
                .toList()));

    loadApiKeySettingsAccountScaffold(mv, apiKey.id());

    mv.addObject("apiKeyId", apiKeyId);

    return mv;
  }

  @PutMapping("/api-keys/{apiKeyId}/info-settings")
  public ModelAndView updateInfoSettings(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("apiKeyId") UUID apiKeyId,
      InfoApiKeySettings form) {

    Optional<ApiKeyFacade.ApiKeyDTO> apiKeyMaybe = this.apiKeyFacade.getById(apiKeyId);

    if (apiKeyMaybe.isEmpty()) {
      throw new RuntimeException();
    }

    ApiKeyFacade.ApiKeyDTO apiKey = apiKeyMaybe.get();

    ModelAndView mv = new ModelAndView("api-key-details/info-settings");

    this.apiKeySettingsFacade.setInfoSettings(
        apiKeyId,
        new ApiKeySettingsFacade.ApiKeySettingsInfoDTO(form.version, form.superGroupTypes));

    loadApiKeySettingsInfo(mv, apiKey.id());

    mv.addObject("apiKeyId", apiKeyId);

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
