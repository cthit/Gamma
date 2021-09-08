package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.group.GroupRepository;
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

@DependsOn("superGroupBootstrap")
@Component
public class GroupBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupBootstrap.class);

    private final MockData mockData;
    private final GroupRepository groupRepository;
    private final boolean mocking;

    public GroupBootstrap(MockData mockData,
                          GroupRepository groupRepository,
                          @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.groupRepository = groupRepository;
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

//        mockData.groups().forEach(mockGroup -> {
//            SuperGroupType type = mockData.superGroups()
//                    .stream()
//                    .filter(sg -> sg.id().equals(mockGroup.superGroupId()))
//                    .findFirst().orElseThrow().type();
//            boolean active = !type.equals(SuperGroupType.valueOf("alumni"));
//            int year = active ? activeYear : inactiveYear;
//            Name name = new Name(mockGroup.name().value() + year);
//            PrettyName prettyName = new PrettyName(mockGroup.prettyName().value() + year);
//
//            Group group = new Group(
//                    mockGroup.id(),
//                    new Email(name + "@chalmers.lol"),
//                    name,
//                    prettyName,
//                    mockGroup.superGroupId(),
//                    mockGroup.members().stream().map(mockMembership -> ).toList()
//            );
//
//            try {
//                this.groupService.create(group);
//
//;
//            } catch (GroupService.GroupAlreadyExistsException e) {
//                LOGGER.error("Group with type " + group.name() + " already exists");
//            }
//        });

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
