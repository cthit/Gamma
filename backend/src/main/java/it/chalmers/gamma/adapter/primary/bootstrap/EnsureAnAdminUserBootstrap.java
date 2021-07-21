package it.chalmers.gamma.adapter.primary.bootstrap;

import it.chalmers.gamma.app.AuthorityFacade;
import it.chalmers.gamma.app.UserFacade;
import it.chalmers.gamma.app.domain.AcceptanceYear;
import it.chalmers.gamma.app.domain.FirstName;
import it.chalmers.gamma.app.domain.LastName;
import it.chalmers.gamma.app.domain.Nick;
import it.chalmers.gamma.app.domain.UnencryptedPassword;
import it.chalmers.gamma.app.domain.AuthorityLevelName;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.domain.UserId;
import it.chalmers.gamma.util.TokenUtils;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.Language;
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
    private final AuthorityFacade authorityFacade;

    public EnsureAnAdminUserBootstrap(UserFacade userFacade, AuthorityFacade authorityFacade) {
        this.userFacade = userFacade;
        this.authorityFacade = authorityFacade;
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
