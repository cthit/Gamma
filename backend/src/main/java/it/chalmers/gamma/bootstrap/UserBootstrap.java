package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.user.PasswordService;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.ImageUri;
import it.chalmers.gamma.domain.user.AcceptanceYear;
import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.domain.user.FirstName;
import it.chalmers.gamma.domain.user.Language;
import it.chalmers.gamma.domain.user.LastName;
import it.chalmers.gamma.domain.user.Nick;
import it.chalmers.gamma.domain.user.UnencryptedPassword;
import it.chalmers.gamma.domain.user.User;
import it.chalmers.gamma.domain.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.stream.Collectors;

@DependsOn("mockBootstrap")
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

    @PostConstruct
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
                        ImageUri.nothing()
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
