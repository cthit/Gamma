package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.group.data.GroupShallowDTO;
import it.chalmers.gamma.domain.membership.data.dto.MembershipShallowDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.post.data.PostDTO;
import it.chalmers.gamma.domain.user.data.UserDTO;
import it.chalmers.gamma.bootstrap.mock.MockData;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class MockBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBootstrap.class);

    private final BootstrapServiceHelper helper;

    private final ResourceLoader resourceLoader;

    public MockBootstrap(BootstrapServiceHelper helper, ResourceLoader resourceLoader) {
        this.helper = helper;
        this.resourceLoader = resourceLoader;
    }

    void runMockBootstrap()  {
        LOGGER.info("Running mock...");

        Resource resource = this.resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        MockData mockData = null;
        try {
            mockData = objectMapper.readValue(resource.getInputStream(), MockData.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error("Error when trying to read mock.json");
            return;
        }

        createUsers(mockData);
        LOGGER.info("Users created");

        createPosts(mockData);
        LOGGER.info("Posts created");

        createSuperGroups(mockData);
        LOGGER.info("Super groups created");

        createGroups(mockData);
        LOGGER.info("Groups created");

        LOGGER.info("Mock finished");
    }

    private void createUsers(MockData mockData) {
        mockData.getUsers().forEach(mockUser -> this.helper.getUserCreationService().createUser(
            new UserDTO.UserDTOBuilder()
                .id(mockUser.getId())
                .nick(mockUser.getNick())
                .firstName(mockUser.getFirstName())
                .lastName(mockUser.getLastName())
                .acceptanceYear(mockUser.getAcceptanceYear())
                .cid(new Cid(mockUser.getCid()))
                .userAgreement(true)
                .email(new Email(mockUser.getCid() + "@student.chalmers.it"))
                .build(),
                "password"
        ));
    }

    private void createPosts(MockData mockData) {
        mockData.getPosts().forEach(mockPost ->
                this.helper.getPostService().create(
                        new PostDTO(
                                mockPost.getId(),
                                mockPost.getPostName(),
                                null
                        )
                )
        );
    }

    private void createGroups(MockData mockData) {
        Calendar activeGroupBecomesActive = toCalendar(
                Instant.now().minus(1, ChronoUnit.DAYS)
        );
        Calendar activeGroupBecomesInactive = toCalendar(
                Instant.now().plus(365, ChronoUnit.DAYS)
        );

        Calendar inactiveGroupBecomesActive = toCalendar(
                Instant.now()
                   .minus(366, ChronoUnit.DAYS)
        );
        Calendar inactiveGroupBecomesInactive = toCalendar(
                Instant.now().minus(1, ChronoUnit.DAYS)
        );

        int activeYear = activeGroupBecomesActive.get(Calendar.YEAR);
        int inactiveYear = inactiveGroupBecomesActive.get(Calendar.YEAR);

        mockData.getGroups().forEach(mockGroup -> {
            int year = mockGroup.isActive() ? activeYear : inactiveYear;
            String name = mockGroup.getName() + year;
            String prettyName = mockGroup.getPrettyName() + year;
            Calendar active = mockGroup.isActive()
                    ? activeGroupBecomesActive
                    : inactiveGroupBecomesActive;
            Calendar inactive = mockGroup.isActive()
                    ? activeGroupBecomesInactive
                    : inactiveGroupBecomesInactive;

            GroupShallowDTO group = new GroupShallowDTO.GroupShallowDTOBuilder()
                    .id(mockGroup.getId())
                    .becomesActive(active)
                    .becomesInactive(inactive)
                    .email(new Email(name + "@chalmers.it"))
                    .name(name)
                    .prettyName(prettyName)
                    .avatarUrl(null)
                    .superGroupId(mockGroup.getSuperGroup())
                    .build();

            try {
                this.helper.getGroupService().create(group);

                mockGroup.getMembers().forEach(mockMembership -> {
                    this.helper.getMembershipService().create(
                            new MembershipShallowDTO(
                                    mockMembership.getPostId(),
                                    mockGroup.getId(),
                                    mockMembership.getUnofficialPostName(),
                                    mockMembership.getUserId()
                            )
                    );
                });
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Error creating group: " + group.getName() + "; Group already exists, skipping...");
            }
        });

    }

    private void createSuperGroups(MockData mockData) {
        mockData.getSuperGroups().forEach(mockSuperGroup -> {
            try {
                this.helper.getSuperGroupService().create(new SuperGroupDTO(
                        mockSuperGroup.getId(),
                        mockSuperGroup.getName(),
                        mockSuperGroup.getPrettyName(),
                        mockSuperGroup.getType(),
                        new Email(mockSuperGroup.getName() + "@chalmers.it"),
                        null));
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Error creating supergroup: " + mockSuperGroup.getName() + "; Super group already exists, skipping...");
            }
        });
    }

    private Calendar toCalendar(Instant i) {
        return GregorianCalendar.from(
            ZonedDateTime.ofInstant(
                    i,
                    ZoneOffset.UTC
            )
        );
    }

}
