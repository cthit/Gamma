package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.bootstrap.mock.MockSuperGroup;
import it.chalmers.gamma.internal.supergrouptype.service.SuperGroupTypeName;
import it.chalmers.gamma.internal.text.data.dto.TextDTO;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.Language;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.internal.group.service.GroupShallowDTO;
import it.chalmers.gamma.internal.membership.service.MembershipShallowDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;
import it.chalmers.gamma.internal.user.service.UserDTO;
import it.chalmers.gamma.bootstrap.mock.MockData;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
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
        mockData.users().forEach(mockUser -> this.helper.getUserCreationService().createUser(
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
                ), "password"
        ));
    }

    private void createPosts(MockData mockData) {
        mockData.posts().forEach(mockPost ->
                this.helper.getPostService().create(
                        new PostDTO(
                                mockPost.id(),
                                mockPost.postName(),
                                null
                        )
                )
        );
    }

    private void createGroups(MockData mockData) {
        Calendar activeGroupBecomesActive = toCalendar(
                Instant.now().minus(1, ChronoUnit.DAYS)
        );
        Calendar inactiveGroupBecomesActive = toCalendar(
                Instant.now()
                   .minus(366, ChronoUnit.DAYS)
        );
        int activeYear = activeGroupBecomesActive.get(Calendar.YEAR);
        int inactiveYear = inactiveGroupBecomesActive.get(Calendar.YEAR);

        mockData.groups().forEach(mockGroup -> {
            SuperGroupTypeName type = mockData.superGroups()
                    .stream()
                    .filter(sg -> sg.id().equals(mockGroup.superGroupId()))
                    .findFirst().orElseThrow().type();
            boolean active = !type.equals(SuperGroupTypeName.valueOf("alumni"));
            int year = active ? activeYear : inactiveYear;
            String name = mockGroup.name() + year;
            String prettyName = mockGroup.prettyName() + year;

            GroupShallowDTO group = new GroupShallowDTO(
                    mockGroup.id(),
                    new Email(name + "@chalmers.lol"),
                    name,
                    prettyName,
                    mockGroup.superGroupId()
            );

            try {
                this.helper.getGroupService().create(group);

                mockGroup.members().forEach(mockMembership -> this.helper.getMembershipService().create(
                        new MembershipShallowDTO(
                                mockMembership.postId(),
                                mockGroup.id(),
                                mockMembership.unofficialPostName(),
                                mockMembership.userId()
                        )
                ));
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Error creating group: " + group.name() + "; Group already exists, skipping...");
            }
        });

    }

    private void createSuperGroups(MockData mockData) {
        mockData.superGroups().stream().map(MockSuperGroup::type).forEach(type -> {
            try {
                this.helper.getSuperGroupTypeService().create(type);
            } catch (EntityAlreadyExistsException ignored) { }
        });

        mockData.superGroups().forEach(mockSuperGroup -> {
            try {
                this.helper.getSuperGroupService().create(new SuperGroupDTO(
                        mockSuperGroup.id(),
                        mockSuperGroup.name(),
                        mockSuperGroup.prettyName(),
                        mockSuperGroup.type(),
                        new Email(mockSuperGroup.name() + "@chalmers.it"),
                        new TextDTO("", "")));
            } catch (EntityAlreadyExistsException e) {
                LOGGER.error("Error creating supergroup: " + mockSuperGroup.name() + "; Super group already exists, skipping...");
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
