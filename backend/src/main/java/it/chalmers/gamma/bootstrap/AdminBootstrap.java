package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.authority.service.AuthorityShallowDTO;
import it.chalmers.gamma.domain.authoritylevel.service.AuthorityLevelName;
import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.group.service.GroupShallowDTO;
import it.chalmers.gamma.domain.membership.service.MembershipShallowDTO;
import it.chalmers.gamma.domain.post.service.PostId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupType;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.domain.post.service.PostDTO;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;
import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.domain.user.service.UserDTO;

import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;

import it.chalmers.gamma.util.domain.*;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
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

    public void runAdminBootstrap() throws AdminBootstrapFailedException {
        String admin = "admin";
        if (!this.helper.getUserFinder().exists(new Cid(admin))) {
            LOGGER.info("Creating admin user, cid:admin, password: " + this.config.getPassword());

            Email adminMail = new Email("admin@chalmers.it");
            String superGroupName = "superadmin";

            SuperGroupId adminSuperGroupId = createAdminSuperGroup(superGroupName, adminMail);
            GroupId adminGroupId = createAdminGroup(admin, adminMail, adminSuperGroupId);
            PostId adminPostId = createAdminPost(admin);
            UserId adminUserId = createAdminUser(admin, adminMail);

            this.helper.getMembershipService().create(
                new MembershipShallowDTO(
                        adminPostId,
                        adminGroupId,
                        null,
                        adminUserId
                )
            );

            try {
                this.helper.getAuthorityLevelService().create(new AuthorityLevelName(admin));
            } catch (EntityAlreadyExistsException e) {
                LOGGER.info("admin authority already exists, not creating a new one...");
            }

            try {
                this.helper.getAuthorityService().create(
                        new AuthorityShallowDTO(
                            adminSuperGroupId,
                            adminPostId,
                            new AuthorityLevelName(admin)
                        )
                );
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }

            LOGGER.info("Admin user created!");
        }
    }

    private SuperGroupId createAdminSuperGroup(String superGroupName, Email adminMail) throws AdminBootstrapFailedException {
        SuperGroupId adminSuperGroupId;

        try {
            adminSuperGroupId = this.helper.getSuperGroupFinder().getByName(superGroupName).id();
        } catch (EntityNotFoundException ignored) {
            adminSuperGroupId = new SuperGroupId();

            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            TextDTO description = new TextDTO(descriptionText, descriptionText);

            try {
                this.helper.getSuperGroupService().create(
                        new SuperGroupDTO(
                                adminSuperGroupId,
                                superGroupName,
                                superGroupName,
                                SuperGroupType.COMMITTEE,
                                adminMail,
                                description
                        )
                );
            } catch (EntityAlreadyExistsException e) {
                throw new AdminBootstrapFailedException();
            }
        }

        return adminSuperGroupId;
    }

    private GroupId createAdminGroup(String admin, Email adminMail, SuperGroupId adminSuperGroupId) throws AdminBootstrapFailedException {
        GroupId adminGroupId;

        try {
            adminGroupId = this.helper.getGroupFinder().getByName(admin).id();
        } catch (EntityNotFoundException ignored) {
            adminGroupId = new GroupId();

            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());

            try {
                this.helper.getGroupService().create(
                        new GroupShallowDTO(
                                adminGroupId,
                                start,
                                end,
                                adminMail,
                                admin,
                                admin,
                                adminSuperGroupId
                        )
                );
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }
        }

        return adminGroupId;
    }

    private PostId createAdminPost(String admin) {
        PostId adminPostId;

        try {
            adminPostId = this.helper.getPostFinder().getBySvName(admin).id();
        } catch (EntityNotFoundException e) {
            TextDTO p = new TextDTO(admin, admin);

            adminPostId = new PostId();

            this.helper.getPostService().create(
                    new PostDTO(
                            adminPostId,
                            p,
                            null
                    )
            );
        }

        return adminPostId;
    }

    private UserId createAdminUser(String admin, Email adminMail) {
        UserId adminUserId = new UserId();

        this.helper.getUserCreationService().createUser(
                new UserDTO(
                        adminUserId,
                        new Cid(admin),
                        adminMail,
                        Language.EN,
                        admin,
                        admin,
                        admin,
                        true,
                        Year.of(2018),
                        true
                ), config.getPassword()
        );

        this.helper.getUserGDPRService().editGDPR(adminUserId, true);

        return adminUserId;
    }

}
