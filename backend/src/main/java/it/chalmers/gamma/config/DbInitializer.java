package it.chalmers.gamma.config;

import it.chalmers.gamma.db.entity.ApiKey;
import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.service.ApiKeyService;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;

import java.time.Instant;
import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * This class adds a superadmin on startup if one does not already exist, to make sure one
 * always exists, and to make development easier.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.ExcessiveParameterList"})
@Component
public class DbInitializer implements CommandLineRunner {   // maybe should be moved to more appropriate package

    private final ITUserService userservice;
    private final FKITGroupService groupService;
    private final AuthorityLevelService authorityLevelService;
    private final PostService postService;
    private final MembershipService membershipService;
    private final AuthorityService authorityService;
    private final ITClientService itClientService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;
    private final ApiKeyService apiKeyService;

    @Value("${application.frontend-client-details.client-id}")
    private String clientId;

    @Value("${application.frontend-client-details.redirect-uri}")
    private String redirectUri;

    private final FKITSuperGroupService fkitSuperGroupService;

    @Value("${application.standard-admin-account.password}")
    private String password;

    @Value("${application.default-oauth2-client.client-name}")
    private String oauth2ClientName;
    @Value("${application.default-oauth2-client.client-id}")
    private String oauth2ClientId;
    @Value("${application.default-oauth2-client.client-secret}")
    private String oauth2ClientSecret;
    @Value("${application.default-oauth2-client.redirect-uri}")
    private String oauth2ClientRedirectUri;
    @Value("${application.default-oauth2-client.api-key}")
    private String oauth2ClientApiKey;
    @Value("${application.default-oauth2-client.mock-client}")
    private boolean isMocking;
    @Value("${application.auth.accessTokenValidityTime}")       // TODO Fix this
    private int accessTokenValidityTime;
    @Value("${application.auth.autoApprove}")
    private boolean autoApprove;
    @Value("${application.auth.refreshTokenValidityTime}")
    private int refreshTokenValidityTime;

    public DbInitializer(ITUserService userservice,
                         FKITGroupService groupService,
                         AuthorityLevelService authorityLevelService,
                         PostService postService,
                         MembershipService membershipService,
                         AuthorityService authorityService,
                         ITClientService itClientService,
                         FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
                         ApiKeyService apiKeyService,
                         FKITSuperGroupService fkitSuperGroupService) {
        this.userservice = userservice;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.itClientService = itClientService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.apiKeyService = apiKeyService;
        this.fkitSuperGroupService = fkitSuperGroupService;
    }

    @Override
    public void run(String... args) {
        ensureAdminUser();
        ensureFrontendClientDetails();
        if (this.isMocking) {
            ensureOauthClient();
        }
    }

    private void ensureFrontendClientDetails() {
        if (!this.itClientService.clientExistsByClientId(this.clientId)) {
            Text description = new Text();
            description.setEn("The client details for the frontend of Gamma");
            description.setSv("Klient detaljerna för Gammas frontend");

            ITClient itClient = new ITClient();
            itClient.setClientId(this.clientId);
            itClient.setClientSecret("{noop}secret");
            itClient.setAutoApprove(true);
            itClient.setName("Gamma Frontend");
            itClient.setCreatedAt(Instant.now());
            itClient.setLastModifiedAt(Instant.now());
            itClient.setRefreshTokenValidity(0);
            this.redirectUri = this.redirectUri.trim();
            itClient.setWebServerRedirectUri(this.redirectUri);
            itClient.setDescription(description);
            itClient.setAccessTokenValidity(60 * 60 * 24 * 30);
            this.itClientService.addITClient(itClient);
        }
    }

    private void ensureAdminUser() {
        String admin = "admin";
        if (!this.userservice.userExists(admin)) {
            Text description = new Text();
            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            description.setEn(descriptionText);
            description.setSv(descriptionText);
            Text function = new Text();
            function.setEn(descriptionText);
            function.setSv(descriptionText);

            CreateSuperGroupRequest superGroupRequest = new CreateSuperGroupRequest();
            superGroupRequest.setName("superadmin");
            superGroupRequest.setPrettyName("super admin");
            superGroupRequest.setType(GroupType.COMMITTEE);
            String adminMail = "admin@chalmers.it";
            superGroupRequest.setEmail(adminMail);
            CreateGroupRequest request = new CreateGroupRequest();
            request.setName("superadmin");
            request.setPrettyName("superAdmin");
            request.setDescription(description);
            request.setFunction(function);
            request.setEmail(adminMail);
            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());
            request.setBecomesActive(start);
            request.setBecomesInactive(end);
            FKITSuperGroup superGroup = this.fkitSuperGroupService.createSuperGroup(superGroupRequest);
            FKITGroup group = this.groupService.createGroup(request);
            this.fkitGroupToSuperGroupService.addRelationship(group, superGroup);
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);
            Post post = this.postService.addPost(p);
            ITUser user = this.userservice.createUser(admin,
                    admin,
                    admin,
                    admin,
                    Year.of(2018),
                    true,
                    adminMail,
                    this.password,
                    Language.sv
            );
            this.membershipService.addUserToGroup(
                    group,
                    user,
                    post,
                    admin
            ); // This might break on a new year
            AuthorityLevel authorityLevel = this.authorityLevelService.addAuthorityLevel(admin);
            this.authorityService.setAuthorityLevel(superGroup, post, authorityLevel);
        }
    }
    private void ensureOauthClient() {
        if (!this.itClientService.clientExistsByClientId(this.oauth2ClientId)) {
            ITClient client = new ITClient();
            client.setName(this.oauth2ClientName);
            Text description = new Text();
            description.setEn("Client for mocking " + this.oauth2ClientName);
            description.setSv("Klient för att mocka " + this.oauth2ClientName);
            client.setDescription(description);
            client.setWebServerRedirectUri(this.oauth2ClientRedirectUri);
            client.setCreatedAt(Instant.now());
            client.setLastModifiedAt(Instant.now());
            client.setAccessTokenValidity(this.accessTokenValidityTime);
            client.setAutoApprove(this.autoApprove);
            client.setRefreshTokenValidity(this.refreshTokenValidityTime);
            client.setClientId(this.oauth2ClientId);
            client.setClientSecret("{noop}" + this.oauth2ClientSecret);
            this.itClientService.addITClient(client);
            ApiKey apiKey = new ApiKey();
            apiKey.setName(this.oauth2ClientName);
            apiKey.setKey(this.oauth2ClientApiKey);

            Text apiDescription = new Text();
            apiDescription.setSv("API key");
            apiDescription.setEn("API key");

            apiKey.setDescription(apiDescription);

            this.apiKeyService.addApiKey(apiKey);
        }
    }
}
