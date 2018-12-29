package it.chalmers.gamma.config;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITClient;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.ITClientService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;

import java.time.Instant;
import java.time.Year;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * This class adds a superadmin on startup if one does not already exist, to make sure one
 * always exists, and to make development easier.
 */
@Component
public class DbInitializer implements CommandLineRunner {   // maybe should be moved to more appropriate package

    private final ITUserService userservice;
    private final FKITService groupService;
    private final AuthorityLevelService authorityLevelService;
    private final PostService postService;
    private final MembershipService membershipService;
    private final AuthorityService authorityService;
    private final ITClientService itClientService;

    @Value("${application.frontend-client-details.client-id}")
    private String clientId;

    @Value("${application.frontend-client-details.client-secret}")
    private String clientSecret;

    @Value("${application.frontend-client-details.redirect-uri}")
    private String redirectUri;

    @Value("${application.standard-admin-account.password}")
    private String password;

    public DbInitializer(ITUserService userService, FKITService groupService,
                         AuthorityLevelService authorityLevelService, PostService postService,
                         MembershipService membershipService, AuthorityService authorityService,
                         ITClientService itClientService) {
        this.userservice = userService;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.itClientService = itClientService;
    }

    @Override
    public void run(String... args) throws Exception {
        ensureAdminUser();
        ensureFrontendClientDetails();
    }

    private void ensureFrontendClientDetails(){
        if(!this.itClientService.clientExistsByClientId(clientId)){
            PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

            Text description = new Text();
            description.setEn("The client details for the frontend of Gamma");
            description.setSv("Klient detaljerna för Gammas frontend");

            ITClient itClient = new ITClient();
            itClient.setClientId(clientId);
            itClient.setClientSecret(passwordEncoder.encode(clientSecret));
            itClient.setAutoApprove(true);
            itClient.setName("Gamma Frontend");
            itClient.setCreatedAt(Instant.now());
            itClient.setLastModifiedAt(Instant.now());
            itClient.setRefreshTokenValidity(0);
            itClient.setWebServerRedirectUri(redirectUri);
            itClient.setDescription(description);
            itClient.setAccessTokenValidity(60 * 60 * 24 * 30);
            this.itClientService.addITClient(itClient);
        }
    }

    private void ensureAdminUser(){
        String admin = "admin";
        if (!this.userservice.userExists(admin)) {
            String adminMail = "admin@chalmers.it";
            Text description = new Text();
            String descriptionText = "Super admin group, do not add anything to this group,"
                + " as it is a way to always keep a privileged user on startup";
            description.setEn(descriptionText);
            description.setSv(descriptionText);
            CreateGroupRequest request = new CreateGroupRequest();
            request.setName("superadmin");
            request.setPrettyName("superAdmin");
            request.setFunc(new Text());
            request.setDescription(description);
            request.setType(GroupType.COMMITTEE);
            request.setEmail(adminMail);
            FKITGroup group = this.groupService.createGroup(request);
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
                this.password
            );
            this.membershipService.addUserToGroup(
                group, user, post, admin, Year.of(2018)
            ); // This might break on a new year
            AuthorityLevel authorityLevel = this.authorityLevelService.addAuthorityLevel(admin);
            this.authorityService.setAuthorityLevel(group, post, authorityLevel);
        }
    }
}
