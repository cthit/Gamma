package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.authoritylevel.AuthorityLevelFacade;
import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevel;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.user.AcceptanceYear;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.domain.user.FirstName;
import it.chalmers.gamma.domain.user.Language;
import it.chalmers.gamma.domain.user.LastName;
import it.chalmers.gamma.domain.user.Nick;
import it.chalmers.gamma.domain.user.UnencryptedPassword;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@DependsOn("adminAuthorityLevelBootstrap")
@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final UserCreationFacade userCreationFacade;
    private final AuthorityLevelFacade authorityLevelFacade;
    private final PasswordEncoder passwordEncoder;

    public EnsureAnAdminUserBootstrap(UserCreationFacade userCreationFacade, AuthorityLevelFacade authorityLevelFacade, PasswordEncoder passwordEncoder) {
        this.userCreationFacade = userCreationFacade;
        this.authorityLevelFacade = authorityLevelFacade;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void ensureAnAdminUser() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        if (!this.authorityLevelFacade.authorityLevelUsed(adminAuthorityLevel)) {
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
                    UnencryptedPassword.valueOf("password").encrypt(passwordEncoder),
                    new FirstName(name),
                    new LastName(name),
                    Instant.now(),
                    new AcceptanceYear(2018),
                    true,
                    false,
                    null,
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            this.userCreationFacade.createUser(adminUser);

            LOGGER.info("Admin user created!");
            LOGGER.info("cid: " + name);
            LOGGER.info("password: " + password);

            this.authorityLevelFacade.create(new AuthorityLevel(
                    adminAuthorityLevel,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    List.of(adminUser)
            ));
        }
    }

}
