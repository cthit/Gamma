package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevel;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.password.PasswordService;
import it.chalmers.gamma.app.user.domain.AcceptanceYear;
import it.chalmers.gamma.app.user.domain.Cid;
import it.chalmers.gamma.app.user.domain.FirstName;
import it.chalmers.gamma.app.user.domain.Language;
import it.chalmers.gamma.app.user.domain.LastName;
import it.chalmers.gamma.app.user.domain.Nick;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword;
import it.chalmers.gamma.app.user.domain.User;
import it.chalmers.gamma.app.user.domain.UserExtended;
import it.chalmers.gamma.app.user.domain.UserId;
import it.chalmers.gamma.app.user.domain.UserRepository;
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
    private final PasswordService passwordService;

    public EnsureAnAdminUserBootstrap(AuthorityLevelFacade authorityLevelFacade,
                                      UserRepository userRepository,
                                      AuthorityLevelRepository authorityLevelRepository,
                                      PasswordService passwordService) {
        this.authorityLevelFacade = authorityLevelFacade;
        this.authorityLevelRepository = authorityLevelRepository;
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }

    public void ensureAnAdminUser() {
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

            User adminUser = new User(
                    adminId,
                    new Cid(name),
                    new Nick(name),
                    new FirstName(name),
                    new LastName(name),
                    new AcceptanceYear(2018),
                    new UserExtended(
                            new Email(name + "@chalmers.it"),
                            0,
                            Language.EN,
                            this.passwordService.encrypt(new UnencryptedPassword("password")),
                            true,
                            true,
                            false,
                            null
                    )
            );

            this.userRepository.save(adminUser);

            LOGGER.info("Admin user created!");
            LOGGER.info("cid: " + name);
            LOGGER.info("password: " + password);

            try {
                this.authorityLevelFacade.addUserToAuthorityLevel(
                        admin,
                        adminUser.id().value()
                );
            } catch (AuthorityLevelFacade.UserNotFoundException | AuthorityLevelFacade.AuthorityLevelNotFoundException e) {
                e.printStackTrace();
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
