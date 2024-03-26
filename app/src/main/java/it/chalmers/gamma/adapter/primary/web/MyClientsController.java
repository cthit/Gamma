package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.ClientFacade;
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
    private String redirectUrl;
    private String prettyName;
    private String svDescription;
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

  @GetMapping("/my-clients/create")
  public ModelAndView getCreateUserClient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/create-user-client");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/create-user-client");
    }

    mv.addObject("form", new CreateUserClient());

    return mv;
  }

  @PostMapping("/my-clients")
  public ModelAndView createUserClient(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      CreateUserClient form,
      BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    ClientFacade.ClientAndApiKeySecrets secrets =
        this.clientFacade.createUserClient(
            new ClientFacade.NewClient(
                form.redirectUrl,
                form.prettyName,
                form.svDescription,
                form.enDescription,
                form.generateApiKey,
                form.emailScope,
                null));

    mv.setViewName("pages/client-credentials");
    mv.addObject("clientUid", secrets.clientUid());
    mv.addObject("clientSecret", secrets.clientSecret());
    mv.addObject("apiKeyToken", secrets.apiKeyToken());
    mv.addObject("name", form.prettyName);

    return mv;
  }
}
