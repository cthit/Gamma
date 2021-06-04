package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.internal.authority.level.service.AuthorityLevelName;
import it.chalmers.gamma.internal.authority.service.AuthorityFinder;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserService;
import it.chalmers.gamma.internal.authority.user.service.AuthorityUserShallowDTO;
import it.chalmers.gamma.internal.user.service.UserCreationService;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Year;

@DependsOn("adminAuthorityLevelBootstrap")
@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final AuthorityFinder authorityFinder;
    private final AuthorityUserService authorityUserService;
    private final UserCreationService userCreationService;

    public EnsureAnAdminUserBootstrap(AuthorityFinder authorityFinder,
                                      AuthorityUserService authorityUserService,
                                      UserCreationService userCreationService) {
        this.authorityFinder = authorityFinder;
        this.authorityUserService = authorityUserService;
        this.userCreationService = userCreationService;
    }

    @PostConstruct
    public void ensureAnAdminUser() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

        if (!this.authorityFinder.authorityLevelUsed(adminAuthorityLevel)) {
            String password = TokenUtils.generateToken(
                    75,
                    TokenUtils.CharacterTypes.LOWERCASE,
                    TokenUtils.CharacterTypes.UPPERCASE,
                    TokenUtils.CharacterTypes.NUMBERS
            );
            int i = 0;
            UserId adminId = null;
            while (i < 100) {
                String name = admin + (i == 0 ? "" : i);
                adminId = new UserId();

                try {
                    this.userCreationService.createUser(
                            new UserDTO(
                                    adminId,
                                    new Cid(name),
                                    new Email(name + "@chalmers.it"),
                                    Language.EN,
                                    Nick.valueOf(name),
                                    FirstName.valueOf(name),
                                    LastName.valueOf(name),
                                    true,
                                    Year.of(2018),
                                    true
                            ),
                            UnencryptedPassword.valueOf(password)
                    );
                    LOGGER.info("Admin user created!");
                    LOGGER.info("cid: " + name);
                    LOGGER.info("password: " + password);
                    break;
                } catch (Exception ignored) {
                }
                i++;
            }

            try {
                this.authorityUserService.create(
                        new AuthorityUserShallowDTO(
                                adminId,
                                adminAuthorityLevel
                        )
                );
            } catch (AuthorityUserService.AuthorityUserNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
