package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.user.Cid;
import it.chalmers.gamma.app.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@DependsOn("mockBootstrap")
@Component
public class UserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBootstrap.class);

    private final MockData mockData;
    private final UserRepository userRepository;
    private final boolean mocking;

    public UserBootstrap(MockData mockData,
                         UserRepository userRepository,
                         @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.userRepository = userRepository;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createUsers() {
        if (!this.mocking || this.userRepository.getAll().stream().anyMatch(user -> !user.cid().getValue().contains("admin"))) {
            return;
        }

        LOGGER.info("========== USER BOOTSTRAP ==========");

//        this.mockData.users().forEach(mockUser -> this.userRepository.create(
//                new User(
//                        mockUser.id(),
//                        mockUser.cid(),
//                        new Email(mockUser.cid() + "@student.chalmers.it"),
//                        Language.EN,
//                        mockUser.nick(),
//                        mockUser.firstName(),
//                        mockUser.lastName(),
//                        Instant.now(),
//                        mockUser.acceptanceYear(),
//                        false,
//                        false,
//                        null
//                ),
//                UnencryptedPassword.valueOf("password")
//        ));

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                        .stream()
                        .map(MockData.MockUser::cid)
                        .map(Cid::getValue)
                        .collect(Collectors.joining(", "))
        );
        LOGGER.info("Use a cid from the row above and use the password: password to sign in");
        LOGGER.info("========== USER BOOTSTRAP ==========");
    }
}
