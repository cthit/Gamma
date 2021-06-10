package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.UnencryptedPassword;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.internal.user.service.UserCreationService;
import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.Language;
import it.chalmers.gamma.internal.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Year;
import java.util.stream.Collectors;

@DependsOn("mockBootstrap")
@Component
public class UserBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBootstrap.class);

    private final MockData mockData;
    private final UserCreationService userCreationService;
    private final UserService userService;
    private final boolean mocking;

    public UserBootstrap(MockData mockData,
                         UserCreationService userCreationService,
                         UserService userService,
                         @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.userCreationService = userCreationService;
        this.userService = userService;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createUsers() {
        if (!this.mocking
                || !this.userService.getAll()
                            .stream()
                            .filter(user -> !user.cid().get().contains("admin"))
                            .collect(Collectors.toList())
                .isEmpty()) {
            return;
        }

        LOGGER.info("========== USER BOOTSTRAP ==========");

        this.mockData.users().forEach(mockUser -> this.userCreationService.createUser(
                new User(
                        mockUser.id(),
                        mockUser.cid(),
                        new Email(mockUser.cid() + "@student.chalmers.it"),
                        Language.EN,
                        mockUser.nick(),
                        mockUser.firstName(),
                        mockUser.lastName(),
                        true,
                        Year.of(mockUser.acceptanceYear()),
                        true
                ),
                UnencryptedPassword.valueOf("password")
        ));

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                        .stream()
                        .map(MockData.MockUser::cid)
                        .map(Cid::get)
                        .collect(Collectors.joining(", "))
        );
        LOGGER.info("Use a cid from the row above and use the password: password to sign in");
        LOGGER.info("========== USER BOOTSTRAP ==========");
    }
}
