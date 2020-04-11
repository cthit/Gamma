package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.util.mock.MockData;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class MockBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockBootstrap.class);

    private final BootstrapServiceHelper hlp;

    private final ResourceLoader resourceLoader;

    public MockBootstrap(BootstrapServiceHelper hlp, ResourceLoader resourceLoader) {
        this.hlp = hlp;
        this.resourceLoader = resourceLoader;
    }

    void runMockBootstrap()  {
        LOGGER.info("Running mock...");

        Resource resource = this.resourceLoader.getResource("classpath:/mock/mock.json");
        ObjectMapper objectMapper = new ObjectMapper();
        MockData mockData = null;
        try {
            mockData = objectMapper.readValue(resource.getFile(), MockData.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        if (mockData == null) {
            LOGGER.error("Error when trying to read mock.json");
            return;
        }

        var users = createUsers(mockData);
        LOGGER.info("Users created");

        var posts = createPosts(mockData);
        LOGGER.info("Posts created");

        var groups = createGroups(mockData, users, posts);
        LOGGER.info("Groups created");

        createSuperGroups(mockData, groups);
        LOGGER.info("Super groups created");

        LOGGER.info("Mock finished");
    }

    private Map<UUID, ITUserDTO> createUsers(MockData mockData) {
        Map<UUID, ITUserDTO> users = new HashMap<>();

        mockData.getUsers().forEach(mockUser -> {
            ITUserDTO user = this.hlp.getUserService().createUser(
                    mockUser.getId(),
                    mockUser.getNick(),
                    mockUser.getFirstName(),
                    mockUser.getLastName(),
                    mockUser.getCid(),
                    mockUser.getAcceptanceYear(),
                    true,
                    mockUser.getCid() + "@student.chalmers.it",
                    "password"
            );

            users.put(user.getId(), user);
        });

        return users;
    }

    private Map<UUID, PostDTO> createPosts(MockData mockData) {
        Map<UUID, PostDTO> posts = new HashMap<>();

        mockData.getPosts().forEach(mockPost -> {
            PostDTO post = this.hlp.getPostService().addPost(
                    mockPost.getId(),
                    mockPost.getPostName()
            );

            posts.put(post.getId(), post);
        });

        return posts;
    }

    private Map<UUID, FKITGroupDTO> createGroups(
            MockData mockData,
            Map<UUID, ITUserDTO> users,
            Map<UUID, PostDTO> posts) {

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

        Map<UUID, FKITGroupDTO> groups = new HashMap<>();

        mockData.getGroups().forEach(mockGroup -> {
            String name = mockGroup.getName() + (mockGroup.isActive() ? activeYear : inactiveYear);
            String prettyName = mockGroup.getPrettyName()
                    + (mockGroup.isActive() ? activeYear : inactiveYear);
            Calendar active = mockGroup.isActive()
                    ? activeGroupBecomesActive
                    : inactiveGroupBecomesActive;
            Calendar inactive = mockGroup.isActive()
                    ? activeGroupBecomesInactive
                    : inactiveGroupBecomesInactive;

            FKITGroupDTO group = new FKITGroupDTO(
                    mockGroup.getId(),
                    active,
                    inactive,
                    mockGroup.getDescription(),
                    name + "@chalmers.it",
                    mockGroup.getFunction(),
                    name,
                    prettyName,
                    null
            );

            groups.put(group.getId(), group);

            this.hlp.getGroupService().createGroup(group);

            mockGroup.getMembers().forEach(mockMembership -> {
                PostDTO post = posts.get(mockMembership.getPostId());
                ITUserDTO user = users.get(mockMembership.getUserId());

                this.hlp.getMembershipService().addUserToGroup(
                        group,
                        user,
                        post,
                        mockMembership.getUnofficialPostName()
                );
            });
        });

        return groups;
    }

    private void createSuperGroups(MockData mockData, Map<UUID, FKITGroupDTO> groups) {
        mockData.getSuperGroups().forEach(mockSuperGroup -> {
            FKITSuperGroupDTO superGroup = new FKITSuperGroupDTO(
                    mockSuperGroup.getId(),
                    mockSuperGroup.getName(),
                    mockSuperGroup.getPrettyName(),
                    mockSuperGroup.getType(),
                    mockSuperGroup.getName() + "@chalmers.it"
            );

            this.hlp.getSuperGroupService().createSuperGroup(superGroup);

            mockSuperGroup.getGroups().forEach(groupId -> {
                FKITGroupDTO group = groups.get(groupId);

                this.hlp.getGroupToSuperGroupService().addRelationship(
                        group,
                        superGroup
                );
            });
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
