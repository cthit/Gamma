package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.internal.user.service.UserCreationService;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.internal.user.service.UserFinder;
import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Year;
import java.util.stream.Collectors;

@Component
public class UserBootstrap {

    private static Logger LOGGER = LoggerFactory.getLogger(UserBootstrap.class);

    private final boolean mocking;
    private final MockData mockData;
    private final UserFinder userFinder;
    private final UserCreationService userCreationService;

    public UserBootstrap(boolean mocking,
                         MockData mockData,
                         UserFinder userFinder,
                         UserCreationService userCreationService) {
        this.mocking = mocking;
        this.mockData = mockData;
        this.userFinder = userFinder;
        this.userCreationService = userCreationService;
    }

    @PostConstruct
    public void createUsers() {
        if (!this.mocking || !this.userFinder.getAll().isEmpty()) {
            return;
        }

        this.mockData.users().forEach(mockUser -> this.userCreationService.createUser(
                new UserDTO(
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
                "password"
        ));

        LOGGER.info("Generated the users: "
                + this.mockData.users()
                        .stream()
                        .map(MockData.MockUser::cid)
                        .map(Cid::get)
                        .collect(Collectors.joining(","))
        );
        LOGGER.info("Use a cid from the row above and use the password: password to sign in");

    }
}
