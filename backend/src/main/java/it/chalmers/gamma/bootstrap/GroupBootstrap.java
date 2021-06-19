package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.group.service.GroupShallowDTO;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import it.chalmers.gamma.internal.membership.service.MembershipShallowDTO;
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
    private final GroupService groupService;
    private final MembershipService membershipService;
    private final boolean mocking;

    public GroupBootstrap(MockData mockData,
                          GroupService groupService,
                          MembershipService membershipService,
                          @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.groupService = groupService;
        this.membershipService = membershipService;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createGroups() {
        if (!this.mocking || !this.groupService.getAll().isEmpty()) {
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
            EntityName name = EntityName.valueOf(mockGroup.name().get() + year);
            PrettyName prettyName = PrettyName.valueOf(mockGroup.prettyName().get() + year);

            GroupShallowDTO group = new GroupShallowDTO(
                    mockGroup.id(),
                    Email.valueOf(name + "@chalmers.lol"),
                    name,
                    prettyName,
                    mockGroup.superGroupId()
            );

            try {
                this.groupService.create(group);

                mockGroup.members().forEach(mockMembership -> this.membershipService.create(
                        new MembershipShallowDTO(
                                mockMembership.postId(),
                                mockGroup.id(),
                                mockMembership.unofficialPostName(),
                                mockMembership.userId()
                        )
                ));
            } catch (GroupService.GroupAlreadyExistsException e) {
                LOGGER.error("Group with type " + group.name() + " already exists");
            }
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
