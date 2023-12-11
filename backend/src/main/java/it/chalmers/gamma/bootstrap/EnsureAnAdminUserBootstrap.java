package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import it.chalmers.gamma.app.user.gdpr.GdprTrainedRepository;
import it.chalmers.gamma.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnsureAnAdminUserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnsureAnAdminUserBootstrap.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final GdprTrainedRepository gdprTrainedRepository;
    private final BootstrapSettings bootstrapSettings;
    private final boolean production;

    public EnsureAnAdminUserBootstrap(UserRepository userRepository,
                                      AdminRepository adminRepository,
                                      GdprTrainedRepository gdprTrainedRepository, BootstrapSettings bootstrapSettings,
                                      @Value("${application.production}")
                                      boolean production) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.gdprTrainedRepository = gdprTrainedRepository;
        this.bootstrapSettings = bootstrapSettings;
        this.production = production;
    }

    public void ensureAnAdminUser() {
        LOGGER.info("========== ENSURE AN ADMIN BOOTSTRAP ==========");

        if (!this.bootstrapSettings.adminSetup()) {
            LOGGER.info("Ensuring admin setup is disabled. I hope you know what you're doing...");
            return;
        }

        if(adminRepository.getAll().size() > 0) {
            LOGGER.info("There is already at least one user that is admin. Not creating a new admin user...");
            return;
        }

        String admin = "admin";

        if (this.userRepository.get(new Cid(admin)).isPresent()) {
            LOGGER.error("There's no user that is admin right now, but there is a user that is named admin. " +
                    "Note that there are no admin users right now and none will be created."
            );
            return;
        }

        String password;
        if(!production) {
            password = "password";
        } else {
            password = TokenUtils.generateToken(
                    75,
                    TokenUtils.CharacterTypes.LOWERCASE,
                    TokenUtils.CharacterTypes.UPPERCASE,
                    TokenUtils.CharacterTypes.NUMBERS
            );
        }

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
                        false,
                        null
                )
        );

        try {
            this.userRepository.create(
                    adminUser,
                    new UnencryptedPassword(password)
            );
        } catch (UserRepository.CidAlreadyInUseException | UserRepository.EmailAlreadyInUseException e) {
            LOGGER.error(e.getMessage());
            return;
        }

        this.adminRepository.setAdmin(adminUser.id(), true);
        this.gdprTrainedRepository.setGdprTrainedStatus(adminUser.id(), true);

        LOGGER.info("Admin user created!");
        LOGGER.info("cid: " + name);
        LOGGER.info("password: " + password);

        LOGGER.info("==========                           ==========");
    }

}
