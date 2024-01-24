package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.client.ClientFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class ClientsController {

    private final ClientFacade clientFacade;
    private final SuperGroupFacade superGroupFacade;

    public ClientsController(ClientFacade clientFacade,
                             SuperGroupFacade superGroupFacade) {
        this.clientFacade = clientFacade;
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/clients")
    public ModelAndView getClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<ClientFacade.ClientDTO> clients = this.clientFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/clients");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/clients");
        }
        mv.addObject("clients", clients);

        return mv;
    }

    @GetMapping("/clients/{id}")
    public ModelAndView getClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID clientUid) {
        Optional<ClientFacade.ClientDTO> client = this.clientFacade.get(clientUid);

        if(client.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/client-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/client-details");
        }
        mv.addObject("client", client.get());

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
            this("",
                    "",
                    "",
                    "",
                    false,
                    false,
                    new ArrayList<>());
        }

        public CreateClient(String redirectUrl,
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
    public ModelAndView createClient(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
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
    public ModelAndView newRestrictionRow(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("partial/new-restriction-to-client");

        mv.addObject("superGroups", this.superGroupFacade.getAll()
                .stream()
                .collect(
                        Collectors.toMap(
                                SuperGroupFacade.SuperGroupDTO::id,
                                SuperGroupFacade.SuperGroupDTO::prettyName
                        )
                )
        );

        return mv;
    }

    @PostMapping("/clients")
    public ModelAndView createClient(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
                                     CreateClient form,
                                     BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView();

        ClientFacade.ClientAndApiKeySecrets secrets = this.clientFacade.create(new ClientFacade.NewClient(
           form.redirectUrl,
           form.prettyName,
           form.svDescription,
           form.enDescription,
           form.generateApiKey,
           form.emailScope,
           new ClientFacade.NewClientRestrictions(
                   form.restrictions
           )
        ));

        mv.setViewName("pages/show-client-credentials");
        mv.addObject("clientUid", secrets.clientUid());
        mv.addObject("clientSecret", secrets.clientSecret());
        mv.addObject("apiKeyToken", secrets.apiKeyToken());

        return mv;
    }

}
