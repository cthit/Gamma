package it.chalmers.gamma.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.post.PostDTO;
import it.chalmers.gamma.user.UserDTO;
import it.chalmers.gamma.bootstrap.mock.MockData;

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

        final Map<UUID, UserDTO> users = createUsers(mockData);
        LOGGER.info("Users created");

        final Map<UUID, PostDTO> posts = createPosts(mockData);
        LOGGER.info("Posts created");

        final Map<UUID, SuperGroupDTO> superGroups = createSuperGroups(mockData);
        LOGGER.info("Super groups created");

        createGroups(mockData, users, posts, superGroups);
        LOGGER.info("Groups created");

        LOGGER.info("Mock finished");
    }

    private Map<UUID, UserDTO> createUsers(MockData mockData) {
        Map<UUID, UserDTO> users = new HashMap<>();

        mockData.getUsers().forEach(mockUser -> {
            UserDTO user = this.helper.getUserService().createUser(
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
            PostDTO post = this.helper.getPostService().addPost(
                    mockPost.getId(),
                    mockPost.getPostName(),
                    ""
            );

            posts.put(post.getId(), post);
        });

        return posts;
    }

    private Map<UUID, GroupDTO> createGroups(
            MockData mockData,
            Map<UUID, UserDTO> users,
            Map<UUID, PostDTO> posts,
            Map<UUID, SuperGroupDTO> superGroups) {

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

        Map<UUID, GroupDTO> groups = new HashMap<>();

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

            GroupDTO group = new GroupDTO.GroupDTOBuilder().id(mockGroup.getId()).becomesActive(active).becomesInactive(inactive).description(mockGroup.getDescription()).email(name + "@chalmers.it").function(mockGroup.getFunction()).name(name).prettyName(prettyName).avatarUrl(null).superGroup(superGroups.get(mockGroup.getSuperGroup())).build();

            groups.put(group.getId(), group);

            this.helper.getGroupService().createGroup(group);

            mockGroup.getMembers().forEach(mockMembership -> {
                PostDTO post = posts.get(mockMembership.getPostId());
                UserDTO user = users.get(mockMembership.getUserId());

                this.helper.getMembershipService().addUserToGroup(
                        group,
                        user,
                        post,
                        mockMembership.getUnofficialPostName()
                );
            });
        });

        return groups;
    }

    private Map<UUID, SuperGroupDTO> createSuperGroups(MockData mockData) {
        Map<UUID, SuperGroupDTO> superGroupDTOMap = new HashMap<>();
        mockData.getSuperGroups().forEach(mockSuperGroup -> {
            SuperGroupDTO superGroup = new SuperGroupDTO(
                    mockSuperGroup.getId(),
                    mockSuperGroup.getName(),
                    mockSuperGroup.getPrettyName(),
                    mockSuperGroup.getType(),
                    mockSuperGroup.getName() + "@chalmers.it"
            );

            superGroupDTOMap.put(superGroup.getId(), this.helper.getSuperGroupService().createSuperGroup(superGroup));
        });
        return superGroupDTOMap;
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
