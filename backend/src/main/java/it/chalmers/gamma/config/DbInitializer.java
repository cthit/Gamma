package it.chalmers.gamma.config;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.response.GetMembershipsResponse;
import it.chalmers.gamma.service.*;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.Year;


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
    @Value("${application.standard-admin-account.password}")
    private String password;

    private final String descriptionText = "Super admin group, do not add anything to this group," +
            " as it is a way to always keep a privileged user on startup";

    public DbInitializer(ITUserService userService, FKITService groupService,
                         AuthorityLevelService authorityLevelService, PostService postService,
                         MembershipService membershipService, AuthorityService authorityService){
        this.userservice = userService;
        this.groupService = groupService;
        this.authorityLevelService = authorityLevelService;
        this.postService = postService;
        this.membershipService = membershipService;
        this.authorityService = authorityService;
    }
    @Override
    public void run(String... args) throws Exception {
        if(!userservice.userExists("admin")) {
            ITUser user = userservice.createUser("admin", "admin",
                    "admin", "admin", Year.of(2018), true,
                    "admin@chalmers.it", password);
            Text description = new Text();
            description.setEn(descriptionText);
            description.setSv(descriptionText);
            FKITGroup group = groupService.createGroup("superadmin", "SuperAdmin", description
                    ,"admin@chalmers.it", GroupType.COMMITTEE, new Text(),null);
            Text p = new Text();
            p.setSv("admin");
            p.setEn("admin");
            Post post = postService.addPost(p);
            membershipService.addUserToGroup(group, user, post, "admin", Year.of(2018)); // This might break on a new year
            AuthorityLevel authorityLevel = authorityLevelService.addAuthorityLevel("admin");
            authorityService.setAuthorityLevel(group, post, authorityLevel);
        }
    }
}
