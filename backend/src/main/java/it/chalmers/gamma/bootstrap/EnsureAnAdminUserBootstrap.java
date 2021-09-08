package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.AuthorityLevelFacade;
import it.chalmers.gamma.app.UserFacade;
import it.chalmers.gamma.domain.authoritylevel.AuthorityLevelName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("adminAuthorityLevelBootstrap")
@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final UserFacade userFacade;
    private final AuthorityLevelFacade authorityLevelFacade;

    public EnsureAnAdminUserBootstrap(UserFacade userFacade, AuthorityLevelFacade authorityLevelFacade) {
        this.userFacade = userFacade;
        this.authorityLevelFacade = authorityLevelFacade;
    }

    @PostConstruct
    public void ensureAnAdminUser() {
        String admin = "admin";
        AuthorityLevelName adminAuthorityLevel = AuthorityLevelName.valueOf(admin);

//        if (!this.authorityFinder.authorityLevelUsed(adminAuthorityLevel)) {
//            String password = TokenUtils.generateToken(
//                    75,
//                    TokenUtils.CharacterTypes.LOWERCASE,
//                    TokenUtils.CharacterTypes.UPPERCASE,
//                    TokenUtils.CharacterTypes.NUMBERS
//            );
//
//            UserId adminId = UserId.generate();
//            String name = "admin";
//            this.userCreationService.createUser(
//                    new User(
//                            adminId,
//                            Cid.valueOf(name),
//                            new Email(name + "@chalmers.it"),
//                            Language.EN,
//                            new Nick(name),
//                            new FirstName(name),
//                            new LastName(name),
//                            true,
//                            new AcceptanceYear(2018)
//                    ),
//                    UnencryptedPassword.valueOf("password")
//            );
//
//            LOGGER.info("Admin user created!");
//            LOGGER.info("cid: " + name);
//            LOGGER.info("password: " + password);
//
//            try {
//                this.authorityUserService.create(
//                        new AuthorityUserShallowDTO(
//                                adminId,
//                                adminAuthorityLevel
//                        )
//                );
//            } catch (AuthorityUserService.AuthorityUserNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        }
    }

}
