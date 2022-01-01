package it.chalmers.gamma.bootstrap;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBootstrap.class);

    private final MockData mockData;
    private final UserRepository userRepository;
    private final boolean mocking;
    private final PasswordService passwordService;

    public UserBootstrap(MockData mockData,
                         UserRepository userRepository,
                         @Value("${application.mocking}") boolean mocking,
                         PasswordService passwordService) {
        this.mockData = mockData;
        this.userRepository = userRepository;
        this.mocking = mocking;
        this.passwordService = passwordService;
    }

    public void createUsers() {
        if (!this.mocking || this.userRepository.getAll().stream().anyMatch(user -> !user.cid().getValue().contains("admin"))) {
            return;
        }

        LOGGER.info("========== USER BOOTSTRAP ==========");

        this.mockData.users().forEach(mockUser -> this.userRepository.save(
                new User(
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
                                this.passwordService.encrypt(new UnencryptedPassword("password")),
                                true,
                                false,
                                false,
                                null
                        )
                )
        ));

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                        .stream()
                        .map(MockData.MockUser::cid)
                        .collect(Collectors.joining(", "))
        );
        LOGGER.info("Use a cid from the row above and use the password: password to sign in");
        LOGGER.info("==========                ==========");
    }
}
