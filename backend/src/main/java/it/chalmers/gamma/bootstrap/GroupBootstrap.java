package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.group.GroupRepository;
import it.chalmers.gamma.app.post.PostRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.user.UserRepository;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.common.ImageUri;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.group.Group;
import it.chalmers.gamma.domain.group.GroupMember;
import it.chalmers.gamma.domain.group.UnofficialPostName;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.domain.user.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

@DependsOn("superGroupBootstrap")
@Component
public class GroupBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupBootstrap.class);

    private final MockData mockData;
    private final GroupRepository groupRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SuperGroupRepository superGroupRepository;
    private final boolean mocking;

    public GroupBootstrap(MockData mockData,
                          GroupRepository groupRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          SuperGroupRepository superGroupRepository,
                          @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.groupRepository = groupRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.superGroupRepository = superGroupRepository;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createGroups() {
        if (!this.mocking || !this.groupRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== GROUP BOOTSTRAP ==========");

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
            Name name = new Name(mockGroup.name().value() + year);
            PrettyName prettyName = new PrettyName(mockGroup.prettyName().value() + year);

            Group group = new Group(
                    mockGroup.id(),
                    new Email(name.value() + "@chalmers.lol"),
                    name,
                    prettyName,
                    superGroupRepository.get(mockGroup.superGroupId()).orElseThrow(),
                    mockGroup.members()
                            .stream()
                            .map(mockMembership -> new GroupMember(
                                    postRepository.get(mockMembership.postId()).orElseThrow(),
                                    Optional.of(mockMembership.unofficialPostName()).orElse(new UnofficialPostName("")),
                                    userRepository.get(mockMembership.userId()).orElseThrow()
                            ))
                            .toList(),
                    ImageUri.nothing(),
                    ImageUri.nothing()
            );

            this.groupRepository.create(group);
        });

        LOGGER.info("========== GROUP BOOTSTRAP ==========");
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
