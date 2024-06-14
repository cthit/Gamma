package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.ClientApprovalFacade;
import it.chalmers.gamma.app.client.ClientAuthorityFacade;
import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.client.domain.authority.ClientAuthorityRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

@Controller
public class ClientsController {

  private final ClientFacade clientFacade;
  private final ClientAuthorityFacade clientAuthorityFacade;
  private final ClientApprovalFacade clientApprovalFacade;
  private final SuperGroupFacade superGroupFacade;
  private final UserFacade userFacade;

  public ClientsController(
      ClientFacade clientFacade,
      ClientAuthorityFacade clientAuthorityFacade,
      ClientApprovalFacade clientApprovalFacade,
      SuperGroupFacade superGroupFacade,
      UserFacade userFacade) {
    this.clientFacade = clientFacade;
    this.clientAuthorityFacade = clientAuthorityFacade;
    this.clientApprovalFacade = clientApprovalFacade;
    this.superGroupFacade = superGroupFacade;
    this.userFacade = userFacade;
  }

  @GetMapping("/clients")
  public ModelAndView getClients(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<ClientFacade.ClientDTO> clients = this.clientFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/official-clients");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/official-clients");
    }
    mv.addObject(
        "clients",
        clients.stream()
            .filter(client -> client.owner() instanceof ClientFacade.ClientDTO.OfficialOwner)
            .toList());

    return mv;
  }

  @GetMapping("/clients/{id}")
  public ModelAndView getClient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String clientUid) {
    if (!isValidUUID(clientUid)) {
      return createClientNotFound(clientUid, htmxRequest);
    }

    UUID uid = UUID.fromString(clientUid);

    Optional<ClientFacade.ClientDTO> client = this.clientFacade.get(uid);

    ModelAndView mv = new ModelAndView();
    if (client.isEmpty()) {
      return createClientNotFound(clientUid, htmxRequest);
    }

    List<ClientAuthorityFacade.ClientAuthorityDTO> clientAuthorities =
        this.clientAuthorityFacade.getAll(uid);
    List<UserFacade.UserDTO> userApprovals = this.clientApprovalFacade.getApprovalsForClient(uid);

    if (htmxRequest) {
      mv.setViewName("pages/client-details");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/client-details");
    }
    mv.addObject("clientUid", client.get().clientUid());
    mv.addObject("client", client.get());
    mv.addObject("clientAuthorities", clientAuthorities);
    mv.addObject(
        "userApprovals",
        userApprovals.stream()
            .sorted(Comparator.comparing(user -> user.nick().toLowerCase()))
            .toList());

    if (client.get().owner() instanceof ClientFacade.ClientDTO.UserOwner userOwner) {
      if (AuthenticationExtractor.getAuthentication() instanceof UserAuthentication userPrincipal) {
        boolean isOwner = userOwner.user().id().equals(userPrincipal.gammaUser().id().value());
        mv.addObject("amIOwner", isOwner);

        if (!isOwner) {
          mv.addObject("owner", userOwner.user());
        }
      }
    }

    return mv;
  }

  public ModelAndView createClientNotFound(String clientUid, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/client-not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/client-not-found");
    }

    mv.addObject("id", clientUid);

    return mv;
  }

  public static final class CreateClient {
    private String redirectUrl;
    private String prettyName;
    private String svDescription;
    private String enDescription;
    private boolean generateApiKey;
    private boolean emailScope;

    private List<UUID> restrictions;

    public CreateClient() {
      this("", "", "", "", false, false, new ArrayList<>());
    }

    public CreateClient(
        String redirectUrl,
        String prettyName,
        String svDescription,
        String enDescription,
        boolean generateApiKey,
        boolean emailScope,
        List<UUID> restrictions) {
      this.redirectUrl = redirectUrl;
      this.prettyName = prettyName;
      this.svDescription = svDescription;
      this.enDescription = enDescription;
      this.generateApiKey = generateApiKey;
      this.emailScope = emailScope;
      this.restrictions = restrictions;
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

    public List<UUID> getRestrictions() {
      return restrictions;
    }

    public void setRestrictions(List<UUID> restrictions) {
      this.restrictions = restrictions;
    }
  }

  @GetMapping("/clients/create")
  public ModelAndView createClient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/create-client");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/create-client");
    }

    mv.addObject("form", new CreateClient());

    return mv;
  }

  @GetMapping("/clients/create/new-restriction")
  public ModelAndView newRestrictionRow(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/add-restriction-to-client");

    mv.addObject(
        "superGroups",
        this.superGroupFacade.getAll().stream()
            .collect(
                Collectors.toMap(
                    SuperGroupFacade.SuperGroupDTO::id,
                    SuperGroupFacade.SuperGroupDTO::prettyName)));
    mv.addObject(
        "superGroupKeys",
        this.superGroupFacade.getAll().stream()
            .sorted(Comparator.comparing(superGroup -> superGroup.prettyName().toLowerCase()))
            .map(SuperGroupFacade.SuperGroupDTO::id)
            .toList());

    return mv;
  }

  @PostMapping("/clients")
  public ModelAndView createClient(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      CreateClient form,
      BindingResult bindingResult,
      HttpServletResponse response) {
    ModelAndView mv = new ModelAndView();

    ClientFacade.CreatedClientDTO result =
        this.clientFacade.createOfficialClient(
            new ClientFacade.NewClient(
                form.redirectUrl,
                form.prettyName,
                form.svDescription,
                form.enDescription,
                form.generateApiKey,
                form.emailScope,
                new ClientFacade.NewClientRestrictions(form.restrictions)));

    mv.setViewName("pages/client-details");

    mv.addObject("clientUid", result.client().clientUid());
    mv.addObject("client", result.client());
    mv.addObject("clientAuthorities", new ArrayList<>());
    mv.addObject("userApprovals", new ArrayList<>());
    mv.addObject("clientSecret", result.clientSecret());
    mv.addObject("apiKeyToken", result.apiKeyToken());

    response.addHeader("HX-Push-Url", "/clients/" + result.client().clientUid());

    return mv;
  }

  @GetMapping("/clients/{id}/authorities")
  public ModelAndView getEditAuthorities(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID clientUid) {
    Optional<ClientFacade.ClientDTO> client = this.clientFacade.get(clientUid);

    if (client.isEmpty()) {
      throw new RuntimeException();
    }

    List<ClientAuthorityFacade.ClientAuthorityDTO> clientAuthorities =
        clientAuthorityFacade.getAll(client.get().clientUid());

    ModelAndView mv = new ModelAndView();

    mv.setViewName("pages/client-details :: client-authorities");
    mv.addObject("client", client.get());
    mv.addObject("clientAuthorities", clientAuthorities);

    return mv;
  }

  @GetMapping("/clients/{id}/new-authority")
  public ModelAndView newAuthority(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID clientUid) {
    Optional<ClientFacade.ClientDTO> client = this.clientFacade.get(clientUid);

    if (client.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/add-authority-to-client");
    mv.addObject("client", client.get());

    return mv;
  }

  @GetMapping("/clients/authority/new-super-group")
  public ModelAndView newSuperGroupAuthority(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/add-super-group-authority-to-client");

    mv.addObject(
        "superGroups",
        this.superGroupFacade.getAll().stream()
            .collect(
                Collectors.toMap(
                    SuperGroupFacade.SuperGroupDTO::id,
                    SuperGroupFacade.SuperGroupDTO::prettyName)));

    return mv;
  }

  @GetMapping("/clients/authority/new-user")
  public ModelAndView newUserToAuthority(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/add-user-authority-to-client");

    mv.addObject(
        "users",
        this.userFacade.getAll().stream()
            .collect(Collectors.toMap(UserFacade.UserDTO::id, UserFacade.UserDTO::nick)));

    return mv;
  }

  public static final class CreateAuthority {
    private List<UUID> superGroups;
    private List<UUID> users;
    private String authority;

    public List<UUID> getSuperGroups() {
      return superGroups;
    }

    public void setSuperGroups(List<UUID> superGroups) {
      this.superGroups = superGroups;
    }

    public List<UUID> getUsers() {
      return users;
    }

    public void setUsers(List<UUID> users) {
      this.users = users;
    }

    public String getAuthority() {
      return authority;
    }

    public void setAuthority(String authority) {
      this.authority = authority;
    }
  }

  @PostMapping("/clients/{id}/authority")
  public ModelAndView createAuthority(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID clientUid,
      CreateAuthority form) {

    try {
      this.clientAuthorityFacade.create(clientUid, form.authority);
    } catch (ClientAuthorityRepository.ClientAuthorityAlreadyExistsException e) {
      throw new RuntimeException(e);
    }

    List<SuperGroupFacade.SuperGroupDTO> superGroups = new ArrayList<>();
    List<UserFacade.UserDTO> users = new ArrayList<>();

    if (form.superGroups != null) {
      form.superGroups.forEach(
          superGroup -> {
            try {
              this.clientAuthorityFacade.addSuperGroupToClientAuthority(
                  clientUid, form.authority, superGroup);
            } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException
                | ClientAuthorityFacade.SuperGroupNotFoundException e) {
              throw new RuntimeException(e);
            }

            superGroups.add(this.superGroupFacade.get(superGroup).orElseThrow());
          });
    }

    if (form.users != null) {
      form.users.forEach(
          user -> {
            try {
              this.clientAuthorityFacade.addUserToClientAuthority(clientUid, form.authority, user);
            } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException
                | ClientAuthorityFacade.UserNotFoundException e) {
              throw new RuntimeException(e);
            }

            users.add(this.userFacade.get(user).orElseThrow().user());
          });
    }

    ModelAndView mv = new ModelAndView("partial/created-client-authority");

    mv.addObject("clientUid", clientUid);
    mv.addObject(
        "clientAuthority",
        new ClientAuthorityFacade.ClientAuthorityDTO(
            clientUid, form.authority, superGroups, users));

    return mv;
  }

  @DeleteMapping("/clients/{id}")
  public ModelAndView deleteClient(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @RequestHeader(value = "owner", required = false) boolean wasOwner,
      @PathVariable("id") UUID clientUid) {
    try {
      this.clientFacade.delete(clientUid);
    } catch (ClientFacade.ClientNotFoundException e) {
      throw new RuntimeException(e);
    }

    if (wasOwner) {
      return new ModelAndView("redirect:/my-clients");
    } else {
      return new ModelAndView("redirect:/clients");
    }
  }

  @DeleteMapping("/clients/{id}/authority/{name}")
  public ModelAndView deleteClientAuthority(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID clientUid,
      @PathVariable("name") String authorityName) {
    try {
      this.clientAuthorityFacade.delete(clientUid, authorityName);
    } catch (ClientAuthorityFacade.ClientAuthorityNotFoundException e) {
      throw new RuntimeException(e);
    }

    return new ModelAndView("partial/deleted-client-authority");
  }
}
