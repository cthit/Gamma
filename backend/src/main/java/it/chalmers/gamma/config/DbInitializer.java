package it.chalmers.gamma.config;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.service.*;

import java.time.Year;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
    private final FKITSuperGroupService fkitSuperGroupService;

    @Value("${application.standard-admin-account.password}")
    private String password;

    public DbInitializer(ITUserService userService, FKITService groupService,
                         AuthorityLevelService authorityLevelService, PostService postService,
                         MembershipService membershipService, AuthorityService authorityService,
                         FKITSuperGroupService fkitSuperGroupService) {
        this.userservice = userService;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
        this.fkitSuperGroupService = fkitSuperGroupService;
    }

    @Override
    public void run(String... args) throws Exception {
        String admin = "admin";
        String adminMail = "admin@chalmers.it";
        if (!this.userservice.userExists(admin)) {
            Text description = new Text();
            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            description.setEn(descriptionText);
            description.setSv(descriptionText);
            CreateSuperGroupRequest superGroupRequest = new CreateSuperGroupRequest();
            superGroupRequest.setName("superadmin");
            superGroupRequest.setPrettyName("super admin");
            superGroupRequest.setType(GroupType.COMMITTEE);
            FKITSuperGroup superGroup = this.fkitSuperGroupService.createSuperGroup(superGroupRequest);
            CreateGroupRequest request = new CreateGroupRequest();
            request.setName("superadmin");
            request.setPrettyName("superAdmin");
            request.setFunc(new Text());
            request.setDescription(description);
            request.setEmail(adminMail);
            FKITGroup group = this.groupService.createGroup(request, superGroup);
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
            this.authorityService.setAuthorityLevel(superGroup, post, authorityLevel);
        }
    }
}
