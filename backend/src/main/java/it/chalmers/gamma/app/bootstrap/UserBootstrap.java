package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.port.service.PasswordService;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.user.AcceptanceYear;
import it.chalmers.gamma.app.domain.user.Cid;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.domain.user.FirstName;
import it.chalmers.gamma.app.domain.user.Language;
import it.chalmers.gamma.app.domain.user.LastName;
import it.chalmers.gamma.app.domain.user.Nick;
import it.chalmers.gamma.app.domain.user.UnencryptedPassword;
import it.chalmers.gamma.app.domain.user.User;
import it.chalmers.gamma.app.domain.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
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

        this.mockData.users().forEach(mockUser -> this.userRepository.create(
                new User(
                        new UserId(mockUser.id()),
                        new Cid(mockUser.cid()),
                        new Email(mockUser.cid() + "@student.chalmers.it"),
                        Language.EN,
                        new Nick(mockUser.nick()),
                        this.passwordService.encrypt(new UnencryptedPassword("password")),
                        new FirstName(mockUser.firstName()),
                        new LastName(mockUser.lastName()),
                        Instant.now(),
                        new AcceptanceYear(mockUser.acceptanceYear()),
                        false,
                        false,
                        Optional.empty()
                )
        ));

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                        .stream()
                        .map(MockData.MockUser::cid)
                        .collect(Collectors.joining(", "))
        );
        LOGGER.info("Use a cid from the row above and use the password: password to sign in");
        LOGGER.info("========== USER BOOTSTRAP ==========");
    }
}
