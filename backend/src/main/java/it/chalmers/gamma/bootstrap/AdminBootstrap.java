package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.authority.AuthorityLevelDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;

import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);

    private final BootstrapConfig cfg;

    private final BootstrapServiceHelper hlp;

    public AdminBootstrap(BootstrapConfig cfg, BootstrapServiceHelper hlp) {
        this.cfg = cfg;
        this.hlp = hlp;
    }

    public void runAdminBootstrap() {
        String admin = "admin";
        if (!this.hlp.getUserService().userExists(admin)) {
            LOGGER.info("Creating admin user, cid:admin, password: " + this.cfg.getPassword());
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
                    this.hlp.getSuperGroupService().createSuperGroup(superGroupCreation);
            FKITGroupDTO group = new FKITGroupDTO(
                    start, end, description, adminMail, function,
                    "superadmin", "superAdmin", null
            );
            group = this.hlp.getGroupService().createGroup(group);
            this.hlp.getGroupToSuperGroupService().addRelationship(group, superGroup);
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);
            PostDTO post = this.hlp.getPostService().addPost(p);
            ITUserDTO user = this.hlp.getUserService().createUser(
                    admin,
                    admin,
                    admin,
                    admin,
                    Year.of(2018),
                    true,
                    adminMail,
                    this.cfg.getPassword()
            );
            this.hlp.getMembershipService().addUserToGroup(
                    group,
                    user,
                    post,
                    admin
            ); // This might break on a new year
            AuthorityLevelDTO authorityLevel = this.hlp.getAuthorityLevelService().addAuthorityLevel(admin);
            this.hlp.getAuthorityService().setAuthorityLevel(superGroup, post, authorityLevel);
            LOGGER.info("admin user created!");
        }
    }

}
