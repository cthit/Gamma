package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.domain.authority.exception.AuthorityAlreadyExistsException;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.authoritylevel.exception.AuthorityLevelAlreadyExistsException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.data.GroupShallowDTO;
import it.chalmers.gamma.domain.group.exception.GroupAlreadyExistsException;
import it.chalmers.gamma.domain.group.exception.GroupNotFoundException;
import it.chalmers.gamma.domain.membership.data.MembershipShallowDTO;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.post.exception.PostNotFoundException;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupAlreadyExistsException;
import it.chalmers.gamma.domain.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.UserId;
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

            SuperGroupId adminSuperGroupId = createAdminSuperGroup(superGroupName, adminMail);
            GroupId adminGroupId = createAdminGroup(admin, adminMail, adminSuperGroupId);
            PostId adminPostId = createAdminPost(admin);
            UserId adminUserId = createAdminUser(admin, adminMail);

            try {
                this.helper.getMembershipService().addMembership(
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
            } catch (AuthorityAlreadyExistsException e) {
                LOGGER.error("Fatal error when creating admin user", e);
                throw new AdminBootstrapFailedException();
            }

            LOGGER.info("Admin user created!");
        }
    }

    private SuperGroupId createAdminSuperGroup(String superGroupName, String adminMail) throws AdminBootstrapFailedException {
        SuperGroupId adminSuperGroupId;

        try {
            adminSuperGroupId = this.helper.getSuperGroupFinder().getSuperGroupByName(superGroupName).getId();
        } catch (SuperGroupNotFoundException ignored) {
            adminSuperGroupId = new SuperGroupId();

            String descriptionText = "Super admin group, do not add anything to this group,"
                    + " as it is a way to always keep a privileged user on startup";
            Text description = new Text(descriptionText, descriptionText);

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

    private GroupId createAdminGroup(String admin, String adminMail, SuperGroupId adminSuperGroupId) throws AdminBootstrapFailedException {
        GroupId adminGroupId;

        try {
            adminGroupId = this.helper.getGroupFinder().getGroupByName(admin).getId();
        } catch (GroupNotFoundException ignored) {
            adminGroupId = new GroupId();

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

    private PostId createAdminPost(String admin) {
        PostId adminPostId;

        try {
            adminPostId = this.helper.getPostFinder().getPostBySvName(admin).getId();
        } catch (PostNotFoundException e) {
            Text p = new Text();
            p.setSv(admin);
            p.setEn(admin);

            adminPostId = new PostId();

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

    private UserId createAdminUser(String admin, String adminMail) {
        UserId adminUserId = new UserId();

        this.helper.getUserCreationService().createUser(
                new UserDTO.UserDTOBuilder()
                        .id(adminUserId)
                        .activated(true)
                        .userAgreement(true)
                        .acceptanceYear(Year.of(2018))
                        .cid(new Cid(admin))
                        .email(new Email(adminMail))
                        .firstName(admin)
                        .lastName(admin)
                        .nick(admin)
                        .language(Language.EN)
                        .build(),
                config.getPassword()
        );

        try {
            this.helper.getUserGDPRService().editGDPR(adminUserId, true);
        } catch (UserNotFoundException e) {
            LOGGER.error("OH NO I KNEW IT", e);
        }

        return adminUserId;
    }

}
