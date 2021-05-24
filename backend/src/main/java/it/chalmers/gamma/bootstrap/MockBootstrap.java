package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.domain.Name;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.internal.group.service.GroupShallowDTO;
import it.chalmers.gamma.internal.membership.service.MembershipShallowDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.post.service.PostDTO;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MockBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBootstrap.class);

    private final BootstrapServiceHelper helper;

    private final ResourceLoader resourceLoader;

    public MockBootstrap(BootstrapServiceHelper helper, ResourceLoader resourceLoader) {
        this.helper = helper;
        this.resourceLoader = resourceLoader;
    }

    //TODO: Only load if mocking
    @Bean
    public MockData mockData() {
        Resource resource = this.resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(resource.getInputStream(), MockData.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error("Error when trying to read mock.json");
            return new MockData(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
    }

    //TODO: add mocking boolean as beanj

    @PostConstruct
    public void runMockBootstrap()  {
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
            SuperGroupType type = mockData.superGroups()
                    .stream()
                    .filter(sg -> sg.id().equals(mockGroup.superGroupId()))
                    .findFirst().orElseThrow().type();
            boolean active = !type.equals(SuperGroupType.valueOf("alumni"));
            int year = active ? activeYear : inactiveYear;
            Name name = Name.valueOf(mockGroup.name().get() + year);
            PrettyName prettyName = PrettyName.valueOf(mockGroup.prettyName().get() + year);

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
        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).forEach(type -> {
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
