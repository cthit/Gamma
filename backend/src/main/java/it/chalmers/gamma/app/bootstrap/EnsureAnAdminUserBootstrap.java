package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.facade.AuthorityLevelFacade;
import it.chalmers.gamma.app.port.service.PasswordService;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.app.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.user.AcceptanceYear;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.domain.user.FirstName;
import it.chalmers.gamma.app.domain.user.Language;
import it.chalmers.gamma.app.domain.user.LastName;
import it.chalmers.gamma.app.domain.user.Nick;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import it.chalmers.gamma.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;

@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final UserRepository userRepository;
    private final AuthorityLevelFacade authorityLevelFacade;
    private final PasswordService passwordService;

    public EnsureAnAdminUserBootstrap(AuthorityLevelFacade authorityLevelFacade,
                                      UserRepository userRepository,
                                      PasswordService passwordService) {
        this.authorityLevelFacade = authorityLevelFacade;
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }

    public void ensureAnAdminUser() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        if (!this.authorityLevelFacade.authorityLevelUsed(adminAuthorityLevel)) {
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
                    Cid.valueOf(name),
                    new Email(name + "@chalmers.it"),
                    Language.EN,
                    new Nick(name),
                    this.passwordService.encrypt(new UnencryptedPassword("password")),
                    new FirstName(name),
                    new LastName(name),
                    Instant.now(),
                    new AcceptanceYear(2018),
                    true,
                    false,
                    ImageUri.nothing()
            );

            this.userRepository.create(adminUser);

            LOGGER.info("Admin user created!");
            LOGGER.info("cid: " + name);
            LOGGER.info("password: " + password);

            this.authorityLevelFacade.addUserToAuthorityLevel(
                    admin,
                    adminUser.id().value()
            );
        }
    }

}
