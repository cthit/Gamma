package it.chalmers.gamma.adapter.bootstrap;

import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.service.SuperGroupService;
import it.chalmers.gamma.app.supergrouptype.service.SuperGroupTypeService;
import it.chalmers.gamma.app.domain.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@DependsOn("postBootstrap")
@Component
public class SuperGroupBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperGroupBootstrap.class);

    private final MockData mockData;
    private final SuperGroupTypeService superGroupTypeService;
    private final SuperGroupService superGroupService;
    private final boolean mocking;

    public SuperGroupBootstrap(MockData mockData,
                               SuperGroupTypeService superGroupTypeService,
                               SuperGroupService superGroupService,
                               @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.superGroupTypeService = superGroupTypeService;
        this.superGroupService = superGroupService;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createSuperGroups() {
        if (!this.mocking || !this.superGroupService.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");

        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).forEach(type -> {
            try {
                this.superGroupTypeService.create(type);
            } catch (SuperGroupTypeService.SuperGroupAlreadyExistsException e) {
                LOGGER.error("Error creating supergroup type: " + type + ";");
            }
        });

        LOGGER.info("Supergroup types created");

        mockData.superGroups().forEach(mockSuperGroup -> {
            try {
                this.superGroupService.create(new SuperGroup(
                        mockSuperGroup.id(),
                        mockSuperGroup.name(),
                        mockSuperGroup.prettyName(),
                        mockSuperGroup.type(),
                        Email.valueOf(mockSuperGroup.name() + "@chalmers.it"),
                        new Text()));
            } catch (SuperGroupService.SuperGroupNotFoundException e) {
                LOGGER.error("Error creating supergroup: " + mockSuperGroup.name() + "; Super group already exists, skipping...");
            }
        });

        LOGGER.info("Supergroups created");

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");
    }

}
