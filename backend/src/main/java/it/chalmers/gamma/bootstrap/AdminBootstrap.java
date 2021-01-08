package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.group.FKITGroupDTO;
import it.chalmers.gamma.domain.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.post.PostDTO;
import it.chalmers.gamma.domain.user.ITUserDTO;

import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);

    private final BootstrapConfig config;

    private final BootstrapServiceHelper helper;

    public AdminBootstrap(BootstrapConfig bootstrapConfig, BootstrapServiceHelper bootstrapServiceHelper) {
        this.config = bootstrapConfig;
        this.helper = bootstrapServiceHelper;
    }

    void runAdminBootstrap() {
        String admin = "admin";
        if (!this.helper.getUserService().userExists(admin)) {
            LOGGER.info("Creating admin user, cid:admin, password: " + this.config.getPassword());
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
                    new FKITSuperGroupDTO(
                            "superadmin",
                        "super admin",
                            GroupType.COMMITTEE,
                            adminMail
                    );
            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());
            FKITSuperGroupDTO superGroup =
                    this.helper.getSuperGroupService().createSuperGroup(superGroupCreation);
            FKITGroupDTO group = new FKITGroupDTO(
                    start, end, description, adminMail, function,
                    "superadmin", "superAdmin", null, superGroup
            );
            group = this.helper.getGroupService().createGroup(group);
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);
            PostDTO post = this.helper.getPostService().addPost(p);
            ITUserDTO user = this.helper.getUserService().createUser(
                    admin,
                    admin,
                    admin,
                    admin,
                    Year.of(2018),
                    true,
                    adminMail,
                    this.config.getPassword()
            );
            this.helper.getMembershipService().addUserToGroup(
                    group,
                    user,
                    post,
                    admin
            ); // This might break on a new year
            AuthorityLevelDTO authorityLevel = this.helper.getAuthorityLevelService().addAuthorityLevel(admin);
            this.helper.getAuthorityService().createAuthority(superGroup, post, authorityLevel);
            LOGGER.info("admin user created!");
        }
    }

}
