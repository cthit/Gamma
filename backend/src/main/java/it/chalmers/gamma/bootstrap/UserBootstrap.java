package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.user.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBootstrap.class);

    private final MockData mockData;
    private final UserRepository userRepository;
    private final BootstrapSettings bootstrapSettings;

    public UserBootstrap(MockData mockData,
                         UserRepository userRepository,
                         BootstrapSettings bootstrapSettings) {
        this.mockData = mockData;
        this.userRepository = userRepository;
        this.bootstrapSettings = bootstrapSettings;
    }

    public void createUsers() {
        if (!this.bootstrapSettings.mocking() || this.userRepository.getAll().size() > 1) {
            return;
        }

        LOGGER.info("========== USER BOOTSTRAP ==========");

        this.mockData.users().forEach(mockUser -> {
            try {
                this.userRepository.create(
                        new GammaUser(
                                new UserId(mockUser.id()),
                                new Cid(mockUser.cid()),
                                new Nick(mockUser.nick()),
                                new FirstName(mockUser.firstName()),
                                new LastName(mockUser.lastName()),
                                new AcceptanceYear(mockUser.acceptanceYear()),
                                Language.EN,
                                new UserExtended(
                                        new Email(mockUser.cid() + "@student.chalmers.it"),
                                        0,
                                        true,
                                        false,
                                        false,
                                        null
                                )
                        ),
                        new UnencryptedPassword("password")
                );
            } catch (UserRepository.CidAlreadyInUseException | UserRepository.EmailAlreadyInUseException e) {
                e.printStackTrace();
            }
        });

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                .stream()
                .map(MockData.MockUser::cid)
                .collect(Collectors.joining(", "))
        );
        LOGGER.info("Use a cid from the row above and use the value: value to sign in");
        LOGGER.info("==========                ==========");
    }
}
