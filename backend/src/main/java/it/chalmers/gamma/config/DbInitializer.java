package it.chalmers.gamma.config;

import it.chalmers.gamma.db.entity.AuthorityLevel;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;

import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.AuthorityService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;

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
            CreateGroupRequest request = new CreateGroupRequest();
            request.setName("superadmin");
            request.setPrettyName("superAdmin");
            request.setFunc(new Text());
            request.setDescription(description);
            request.setEmail(adminMail);
            request.setYear(2018);
            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());
            request.setBecomesActive(start);
            request.setBecomesInactive(end);
            FKITSuperGroup superGroup = this.fkitSuperGroupService.createSuperGroup(superGroupRequest);
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
                    group, user, post, admin); // This might break on a new year
            AuthorityLevel authorityLevel = this.authorityLevelService.addAuthorityLevel(admin);
            this.authorityService.setAuthorityLevel(superGroup, post, authorityLevel);
        }
    }
}
