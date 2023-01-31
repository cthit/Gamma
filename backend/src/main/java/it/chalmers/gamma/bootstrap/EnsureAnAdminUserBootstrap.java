package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final UserRepository userRepository;
    private final AuthorityLevelFacade authorityLevelFacade;
    private final AuthorityLevelRepository authorityLevelRepository;
    private final BootstrapSettings bootstrapSettings;

    public EnsureAnAdminUserBootstrap(AuthorityLevelFacade authorityLevelFacade,
                                      UserRepository userRepository,
                                      AuthorityLevelRepository authorityLevelRepository,
                                      BootstrapSettings bootstrapSettings) {
        this.authorityLevelFacade = authorityLevelFacade;
        this.authorityLevelRepository = authorityLevelRepository;
        this.userRepository = userRepository;
        this.bootstrapSettings = bootstrapSettings;
    }

    public void ensureAnAdminUser() {
        if (!this.bootstrapSettings.adminSetup()) {
            LOGGER.info("Admin setup is disabled. I hope you know what you're doing...");
            return;
        }

        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        if (!authorityLevelUsed(adminAuthorityLevel)) {
            LOGGER.info("========== ENSURE AN ADMIN BOOTSTRAP ==========");

            if (this.userRepository.get(new Cid(admin)).isPresent()) {
                LOGGER.error("There's no user that is admin right now, but there is a user that is named admin. Doing nothing about this...");
                return;
            }

            String password = TokenUtils.generateToken(
                    75,
                    TokenUtils.CharacterTypes.LOWERCASE,
                    TokenUtils.CharacterTypes.UPPERCASE,
                    TokenUtils.CharacterTypes.NUMBERS
            );

            UserId adminId = UserId.generate();
            String name = "admin";

            GammaUser adminUser = new GammaUser(
                    adminId,
                    new Cid(name),
                    new Nick(name),
                    new FirstName(name),
                    new LastName(name),
                    new AcceptanceYear(2018),
                    Language.EN,
                    new UserExtended(
                            new Email(name + "@chalmers.it"),
                            0,
                            true,
                            true,
                            false,
                            null
                    )
            );

            try {
                this.userRepository.create(
                        adminUser,
                        new UnencryptedPassword("password")
                );
            } catch (UserRepository.CidAlreadyInUseException | UserRepository.EmailAlreadyInUseException e) {
                return;
            }

            LOGGER.info("Admin user created!");
            LOGGER.info("cid: " + name);
            LOGGER.info("value: " + password);

            try {
                this.authorityLevelFacade.addUserToAuthorityLevel(
                        admin,
                        adminUser.id().value()
                );
            } catch (AuthorityLevelFacade.UserNotFoundException |
                     AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
                LOGGER.error("Failed to add user to authority level", e);
            }

            LOGGER.info("==========                           ==========");
        }
    }

    public boolean authorityLevelUsed(AuthorityLevelName adminAuthorityLevel) {
        Optional<AuthorityLevel> maybeAuthorityLevel = this.authorityLevelRepository.get(adminAuthorityLevel);
        if (maybeAuthorityLevel.isEmpty()) {
            return false;
        }

        AuthorityLevel authorityLevel = maybeAuthorityLevel.get();

        return !authorityLevel.posts().isEmpty()
                || !authorityLevel.users().isEmpty()
                || !authorityLevel.superGroups().isEmpty();
    }

}
