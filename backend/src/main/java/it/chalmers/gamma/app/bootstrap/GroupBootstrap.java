package it.chalmers.gamma.app.bootstrap;

import it.chalmers.gamma.app.port.repository.GroupRepository;
import it.chalmers.gamma.app.port.repository.PostRepository;
import it.chalmers.gamma.app.port.repository.SuperGroupRepository;
import it.chalmers.gamma.app.port.repository.UserRepository;
import it.chalmers.gamma.app.domain.common.Email;
import it.chalmers.gamma.app.domain.common.ImageUri;
import it.chalmers.gamma.app.domain.common.PrettyName;
import it.chalmers.gamma.app.domain.group.Group;
import it.chalmers.gamma.app.domain.group.GroupId;
import it.chalmers.gamma.app.domain.group.GroupMember;
import it.chalmers.gamma.app.domain.group.UnofficialPostName;
import it.chalmers.gamma.app.domain.post.PostId;
import it.chalmers.gamma.app.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.app.domain.user.Name;
import it.chalmers.gamma.app.domain.user.UserId;
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
            System.out.println(type);
            boolean active = !type.equalsIgnoreCase("alumni");
            int year = active ? activeYear : inactiveYear;
            Name name = new Name(mockGroup.name() + year);
            PrettyName prettyName = new PrettyName(mockGroup.prettyName() + year);

            Group group = new Group(
                    new GroupId(mockGroup.id()),
                    0,
                    new Email(name.value() + "@chalmers.lol"),
                    name,
                    prettyName,
                    superGroupRepository.get(new SuperGroupId(mockGroup.superGroupId())).orElseThrow(),
                    mockGroup.members()
                            .stream()
                            .map(mockMembership -> new GroupMember(
                                    postRepository.get(new PostId(mockMembership.postId())).orElseThrow(),
                                    mockMembership.unofficialPostName() == null
                                            ? new UnofficialPostName("")
                                            : new UnofficialPostName(mockMembership.unofficialPostName()),
                                    userRepository.get(new UserId(mockMembership.userId())).orElseThrow()
                            ))
                            .toList(),
                    Optional.empty(),
                    Optional.empty()
            );

            this.groupRepository.save(group);
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
