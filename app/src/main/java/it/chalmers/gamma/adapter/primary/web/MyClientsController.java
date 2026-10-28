package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.client.domain.ClientRedirectUrl.ClientRedirectUrlValidator;
import it.chalmers.gamma.app.common.PrettyName.PrettyNameValidator;
import it.chalmers.gamma.app.common.TextValue;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MyClientsController {

  private final ClientFacade clientFacade;

  public MyClientsController(ClientFacade clientFacade) {
    this.clientFacade = clientFacade;
  }

  @GetMapping("/my-clients")
  public ModelAndView getMyClients(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/my-clients");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/my-clients");
    }

    mv.addObject("clients", clientFacade.getAllMyClients());

    return mv;
  }

  public static final class CreateUserClient {
    @ValidatedWith(ClientRedirectUrlValidator.class)
    private String redirectUrl;

    @ValidatedWith(PrettyNameValidator.class)
    private String prettyName;

    @ValidatedWith(TextValue.TextValueValidator.class)
    private String svDescription;

    @ValidatedWith(TextValue.TextValueValidator.class)
    private String enDescription;

    private boolean generateApiKey;
    private boolean emailScope;

    public CreateUserClient() {
      this("", "", "", "", false, false);
    }

    public CreateUserClient(
        String redirectUrl,
        String prettyName,
        String svDescription,
        String enDescription,
        boolean generateApiKey,
        boolean emailScope) {
      this.redirectUrl = redirectUrl;
      this.prettyName = prettyName;
      this.svDescription = svDescription;
      this.enDescription = enDescription;
      this.generateApiKey = generateApiKey;
      this.emailScope = emailScope;
    }

    public String getRedirectUrl() {
      return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
      this.redirectUrl = redirectUrl;
    }

    public String getPrettyName() {
      return prettyName;
    }

    public void setPrettyName(String prettyName) {
      this.prettyName = prettyName;
    }

    public String getSvDescription() {
      return svDescription;
    }

    public void setSvDescription(String svDescription) {
      this.svDescription = svDescription;
    }

    public String getEnDescription() {
      return enDescription;
    }

    public void setEnDescription(String enDescription) {
      this.enDescription = enDescription;
    }

    public boolean isGenerateApiKey() {
      return generateApiKey;
    }

    public void setGenerateApiKey(boolean generateApiKey) {
      this.generateApiKey = generateApiKey;
    }

    public boolean isEmailScope() {
      return emailScope;
    }

    public void setEmailScope(boolean emailScope) {
      this.emailScope = emailScope;
    }
  }

  private ModelAndView createGetCreateUserClient(
      boolean htmxRequest, CreateUserClient form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (form == null) {
      form = new CreateUserClient();
    }

    if (htmxRequest) {
      mv.setViewName("pages/create-user-client");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/create-user-client");
    }

    mv.addObject("form", form);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/my-clients/create")
  public ModelAndView getCreateUserClient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetCreateUserClient(htmxRequest, null, null);
  }

  @PostMapping("/my-clients")
  public ModelAndView createUserClient(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      CreateUserClient form,
      BindingResult bindingResult,
      HttpServletResponse response) {
    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      // A boosted form pushes its action url (/my-clients) by default; keep the current url.
      response.addHeader("HX-Push-Url", "false");
      return createGetCreateUserClient(htmxRequest, form, bindingResult);
    }

    ModelAndView mv = new ModelAndView();

    ClientFacade.CreatedClientDTO result =
        this.clientFacade.createUserClient(
            new ClientFacade.NewClient(
                form.redirectUrl,
                form.prettyName,
                form.svDescription,
                form.enDescription,
                form.generateApiKey,
                form.emailScope,
                null));

    mv.setViewName("client-details/page");

    mv.addObject("clientUid", result.client().clientUid());
    mv.addObject("client", result.client());
    mv.addObject("clientAuthorities", new ArrayList<>());
    mv.addObject("userApprovals", new ArrayList<>());
    mv.addObject("clientSecret", result.clientSecret());
    mv.addObject("apiKeyToken", result.apiKeyToken());
    mv.addObject("amIOwner", true);

    response.addHeader("HX-Push-Url", "/clients/" + result.client().clientUid().toString());

    return mv;
  }
}
