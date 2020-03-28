package it.chalmers.gamma.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.authority.AuthorityDTO;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
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
import it.chalmers.gamma.util.mock.MockData;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;


/**
 * This class adds a superadmin on startup if one does not already exist, to make sure one
 * always exists, and to make development easier. It also adds mock data from /mock/mock.json
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.ExcessiveParameterList"})
@Component
public class DbInitializer implements CommandLineRunner {   // maybe should be moved to more appropriate package

    private static final Logger LOGGER = LoggerFactory.getLogger(DbInitializer.class);

    private final ITUserService userService;
    private final FKITGroupService groupService;
    private final AuthorityLevelService authorityLevelService;
    private final PostService postService;
    private final MembershipService membershipService;
    private final AuthorityService authorityService;
    private final ITClientService itClientService;
    private final FKITGroupToSuperGroupService groupToSuperGroupService;
    private final ApiKeyService apiKeyService;

    @Value("${application.frontend-client-details.client-id}")
    private String clientId;

    @Value("${application.frontend-client-details.redirect-uri}")
    private String redirectUri;

    private final FKITSuperGroupService superGroupService;

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

    @Autowired
    private ResourceLoader resourceLoader;

    private static final String ADMIN_GROUP_NAME = "digit";
    private static final String GPDR_GROUP_NAME = "dpo";

    public DbInitializer(ITUserService userService,
                         FKITGroupService groupService,
                         AuthorityLevelService authorityLevelService,
                         PostService postService,
                         MembershipService membershipService,
                         AuthorityService authorityService,
                         ITClientService itClientService,
                         FKITGroupToSuperGroupService groupToSuperGroupService,
                         ApiKeyService apiKeyService,
                         FKITSuperGroupService superGroupService) {
        this.userService = userService;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.itClientService = itClientService;
        this.groupToSuperGroupService = groupToSuperGroupService;
        this.apiKeyService = apiKeyService;
        this.superGroupService = superGroupService;
    }

    @Override
    public void run(String... args) {
        if(this.isMocking && !this.userService.userExists("admin")){
            LOGGER.info("Running mock...");
            runMock();
            LOGGER.info("Mock finished");
        }

        ensureAdminUser();
        ensureFrontendClientDetails();
        if (this.isMocking) {
            ensureOauthClient();
        }
    }

    private void ensureFrontendClientDetails() {
        if (!this.itClientService.clientExists(this.clientId)) {
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
        if (!this.userService.userExists(admin)) {
            Text description = new Text();
            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            description.setEn(descriptionText);
            description.setSv(descriptionText);
            Text function = new Text();
            function.setEn(descriptionText);
            function.setSv(descriptionText);
            String adminMail = "admin@chalmers.it";
            FKITSuperGroupDTO superGroupCreation =
                    new FKITSuperGroupDTO("superadmin", "super admin", GroupType.COMMITTEE, adminMail);
            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());
            FKITSuperGroupDTO superGroup = this.superGroupService.createSuperGroup(superGroupCreation);
            FKITGroupDTO group = new FKITGroupDTO(
                    start, end, description, adminMail, function, "superadmin", "superAdmin", null
            );
            group = this.groupService.createGroup(group);
            this.groupToSuperGroupService.addRelationship(group, superGroup);
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);
            PostDTO post = this.postService.addPost(p);
            ITUserDTO user = this.userService.createUser(admin,
                    admin,
                    admin,
                    admin,
                    Year.of(2018),
                    true,
                    adminMail,
                    this.password
            );
            this.membershipService.addUserToGroup(
                    group,
                    user,
                    post,
                    admin
            ); // This might break on a new year
            AuthorityLevelDTO authorityLevel = this.authorityLevelService.addAuthorityLevel(admin);
            this.authorityService.setAuthorityLevel(superGroup, post, authorityLevel);
        }
    }

    private void ensureOauthClient() {
        if (!this.itClientService.clientExists(this.oauth2ClientId)) {
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
            Text apiDescription = new Text();
            apiDescription.setSv("API key");
            apiDescription.setEn("API key");
            this.apiKeyService.addApiKey(this.oauth2ClientName, this.oauth2ClientApiKey, apiDescription);
        }
    }

    private void runMock(){
        Resource resource = resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        MockData mockData = null;
        try {
            mockData = objectMapper.readValue(resource.getFile(), MockData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(mockData == null){
            new Exception("Error when reading mock data").printStackTrace();
            return;
        }

        Map<UUID, ITUserDTO> users = new HashMap<>();

        mockData.getUsers().forEach(mockUser -> {
            ITUserDTO user = userService.createUser(
                mockUser.getId(),
                mockUser.getNick(),
                mockUser.getFirstName(),
                mockUser.getLastName(),
                mockUser.getCid(),
                mockUser.getAcceptanceYear(),
                true,
                mockUser.getCid() + "@student.chalmers.it", //bogus "it" student mail (if in anycase the student.chalmers.se mail actually exists
                "password"
            );

            users.put(user.getId(), user);
        });

        Map<UUID, PostDTO> posts = new HashMap<>();

        mockData.getPosts().forEach(mockPost -> {
            PostDTO post = postService.addPost(
                mockPost.getId(),
                mockPost.getPostName()
            );

            posts.put(post.getId(), post);
        });


        Calendar activeGroupBecomesActive = toCalendar(
            Instant.now().minus(1, ChronoUnit.DAYS)
        );
        Calendar activeGroupBecomesInactive = toCalendar(
            Instant.now().plus(365, ChronoUnit.DAYS)
        );

        Calendar inactiveGroupBecomesActive = toCalendar(
            Instant.now()
                .minus(366, ChronoUnit.DAYS)
        );
        Calendar inactiveGroupBecomesInactive = toCalendar(
            Instant.now().minus(1, ChronoUnit.DAYS)
        );

        int activeYear = activeGroupBecomesActive.get(Calendar.YEAR);
        int inactiveYear = inactiveGroupBecomesActive.get(Calendar.YEAR);

        Map<UUID, FKITGroupDTO> groups = new HashMap<>();

        mockData.getGroups().forEach(mockGroup -> {
            String name = mockGroup.getName() + (mockGroup.isActive() ? activeYear : inactiveYear);
            String prettyName = mockGroup.getPrettyName() + (mockGroup.isActive() ? activeYear : inactiveYear);
            Calendar active = mockGroup.isActive()
                    ? activeGroupBecomesActive
                    : inactiveGroupBecomesActive;
            Calendar inactive = mockGroup.isActive()
                    ? activeGroupBecomesInactive
                    : inactiveGroupBecomesInactive;

            FKITGroupDTO group = new FKITGroupDTO(
                mockGroup.getId(),
                active,
                inactive,
                mockGroup.getDescription(),
                name + "@chalmers.it",
                mockGroup.getFunction(),
                name,
                prettyName,
                null
            );

            groups.put(group.getId(), group);

            groupService.createGroup(group);

            mockGroup.getMembers().forEach(mockMembership -> {
                PostDTO post = posts.get(mockMembership.getPostId());
                ITUserDTO user = users.get(mockMembership.getUserId());

                membershipService.addUserToGroup(
                        group,
                        user,
                        post,
                        mockMembership.getUnofficialPostName()
                );
            });
        });

        mockData.getSuperGroups().forEach(mockSuperGroup -> {
            FKITSuperGroupDTO superGroup = new FKITSuperGroupDTO(
                mockSuperGroup.getId(),
                mockSuperGroup.getName(),
                mockSuperGroup.getPrettyName(),
                mockSuperGroup.getType(),
                mockSuperGroup.getName() + "@chalmers.it"
            );

            superGroupService.createSuperGroup(superGroup);

            mockSuperGroup.getGroups().forEach(groupId -> {
                FKITGroupDTO group = groups.get(groupId);

                groupToSuperGroupService.addRelationship(
                    group,
                    superGroup
                );
            });
        });
    }

    private Calendar toCalendar(Instant i){
        return GregorianCalendar.from(
                ZonedDateTime.ofInstant(
                        i,
                        ZoneId.systemDefault()
                )
        );
    }

}
