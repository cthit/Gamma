package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.authority.exception.AuthorityAlreadyExists;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.domain.group.data.GroupShallowDTO;
import it.chalmers.gamma.domain.group.exception.GroupAlreadyExistsException;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.data.MembershipShallowDTO;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupAlreadyExistsException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserDTO;

import java.time.Year;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import it.chalmers.gamma.domain.user.exception.UserNotFoundException;
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
        if (!this.helper.getUserFinder().userExists(new Cid(admin))) {
            LOGGER.info("Creating admin user, cid:admin, password: " + this.config.getPassword());

            String adminMail = "admin@chalmers.it";
            String superGroupName = "superadmin";

            UUID adminSuperGroupId = createAdminSuperGroup(superGroupName, adminMail);
            UUID adminGroupId = createAdminGroup(admin, adminMail, adminSuperGroupId);
            UUID adminPostId = createAdminPost(admin);
            UUID adminUserId = createAdminUser(admin, adminMail);

            try {
                this.helper.getMembershipService().addUserToGroup(
                    new MembershipShallowDTO(
                            adminPostId,
                            adminGroupId,
                            null,
                            adminUserId
                    )
                );
            } catch (GroupNotFoundException | PostNotFoundException | UserNotFoundException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }

            try {
                this.helper.getAuthorityLevelService().addAuthorityLevel(new AuthorityLevelName(admin));
            } catch (AuthorityLevelAlreadyExistsException e) {
                LOGGER.info("admin authority already exists, not creating a new one...");
            }

            try {
                this.helper.getAuthorityService().createAuthority(
                        adminSuperGroupId,
                        adminPostId,
                        new AuthorityLevelName(admin)
                );
            } catch (AuthorityAlreadyExists e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }

            LOGGER.info("Admin user created!");
        }
    }

    private UUID createAdminSuperGroup(String superGroupName, String adminMail) throws AdminBootstrapFailedException {
        UUID adminSuperGroupId;

        try {
            adminSuperGroupId = this.helper.getSuperGroupFinder().getSuperGroupByName(superGroupName).getId();
        } catch (SuperGroupNotFoundException ignored) {
            adminSuperGroupId = UUID.randomUUID();

            Text description = new Text();
            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            description.setEn(descriptionText);
            description.setSv(descriptionText);

            try {
                this.helper.getSuperGroupService().createSuperGroup(
                        new SuperGroupDTO(
                                adminSuperGroupId,
                                superGroupName,
                                superGroupName,
                                GroupType.COMMITTEE,
                                adminMail,
                                description
                        )
                );
            } catch (SuperGroupAlreadyExistsException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }
        }

        return adminSuperGroupId;
    }

    private UUID createAdminGroup(String admin, String adminMail, UUID adminSuperGroupId) throws AdminBootstrapFailedException {
        UUID adminGroupId;

        try {
            adminGroupId = this.helper.getGroupFinder().getGroupByName(admin).getId();
        } catch (GroupNotFoundException ignored) {
            adminGroupId = UUID.randomUUID();

            Calendar end = new GregorianCalendar();
            end.set(2099, Calendar.DECEMBER, 31);
            Calendar start = new GregorianCalendar();
            start.setTimeInMillis(System.currentTimeMillis());

            try {
                this.helper.getGroupService().createGroup(
                        new GroupShallowDTO.GroupShallowDTOBuilder()
                                .id(adminGroupId)
                                .becomesActive(start)
                                .becomesInactive(end)
                                .email(new Email(adminMail))
                                .name(admin)
                                .prettyName(admin)
                                .superGroupId(adminSuperGroupId)
                                .build()
                );
            } catch (GroupAlreadyExistsException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }
        }

        return adminGroupId;
    }

    private UUID createAdminPost(String admin) {
        UUID adminPostId;

        try {
            adminPostId = this.helper.getPostFinder().getPostBySvName(admin).getId();
        } catch (PostNotFoundException e) {
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);

            adminPostId = UUID.randomUUID();

            this.helper.getPostService().addPost(
                    new PostDTO(
                            adminPostId,
                            p,
                            null
                    )
            );
        }

        return adminPostId;
    }

    private UUID createAdminUser(String admin, String adminMail) {
        UUID adminUserId = UUID.randomUUID();

        this.helper.getUserCreationService().createUser(
                new UserDTO.UserDTOBuilder()
                        .id(adminUserId)
                        .userAgreement(true)
                        .acceptanceYear(Year.of(2018))
                        .cid(new Cid(admin))
                        .email(new Email(adminMail))
                        .accountLocked(false)
                        .firstName(admin)
                        .lastName(admin)
                        .gdpr(true)
                        .nick(admin)
                        .language(Language.EN)
                        .build(),
                config.getPassword()
        );

        return adminUserId;
    }

}
