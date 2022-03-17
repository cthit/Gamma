package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.group.domain.GroupRepository;
import it.chalmers.gamma.app.post.domain.PostRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.user.domain.UserRepository;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.group.domain.Group;
import it.chalmers.gamma.app.group.domain.GroupId;
import it.chalmers.gamma.app.group.domain.GroupMember;
import it.chalmers.gamma.app.group.domain.UnofficialPostName;
import it.chalmers.gamma.app.post.domain.PostId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.user.domain.Name;
import it.chalmers.gamma.app.user.domain.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

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
            String type = mockData.superGroups()
                    .stream()
                    .filter(sg -> sg.id().equals(mockGroup.superGroupId()))
                    .findFirst()
                    .orElseThrow()
                    .type();

            boolean active = !type.equalsIgnoreCase("alumni");
            int year = active ? activeYear : inactiveYear;
            Name name = new Name(mockGroup.name() + year);
            PrettyName prettyName = new PrettyName(mockGroup.prettyName() + year);

            Group group = new Group(
                    new GroupId(mockGroup.id()),
                    0,
                    name,
                    prettyName,
                    superGroupRepository.get(new SuperGroupId(mockGroup.superGroupId())).orElseThrow(),
                    mockGroup.members()
                            .stream()
                            .map(mockMembership -> new GroupMember(
                                    postRepository.get(new PostId(mockMembership.postId())).orElseThrow(),
                                    mockMembership.unofficialPostName() == null
                                            ? UnofficialPostName.none()
                                            : new UnofficialPostName(mockMembership.unofficialPostName()),
                                    userRepository.get(new UserId(mockMembership.userId()))
                                            .orElseThrow(() -> {
                                                System.out.println("Hjälp: " + mockMembership.userId());
                                                return new IllegalArgumentException();
                                            })
                            ))
                            .toList(),
                    Optional.empty(),
                    Optional.empty()
            );

            try {
                this.groupRepository.save(group);
            } catch (GroupRepository.GroupNameAlreadyExistsException e) {
                e.printStackTrace();
            }
        });

        LOGGER.info("==========                 ==========");
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
