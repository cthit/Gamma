package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.SuperGroupTypeRepository;
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
    private final SuperGroupTypeRepository superGroupTypeRepository;
    private final SuperGroupRepository superGroupRepository;
    private final boolean mocking;

    public SuperGroupBootstrap(MockData mockData,
                               SuperGroupTypeRepository superGroupTypeRepository,
                               SuperGroupRepository superGroupRepository,
                               @Value("${application.mocking}") boolean mocking) {
        this.mockData = mockData;
        this.superGroupTypeRepository = superGroupTypeRepository;
        this.superGroupRepository = superGroupRepository;
        this.mocking = mocking;
    }

    @PostConstruct
    public void createSuperGroups() {
        if (!this.mocking || !this.superGroupRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");

        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).forEach(type -> {
            try {
                this.superGroupTypeRepository.create(type);
            } catch (SuperGroupTypeService.SuperGroupAlreadyExistsException e) {
                LOGGER.error("Error creating supergroup type: " + type + ";");
            }
        });

        LOGGER.info("Supergroup types created");

        mockData.superGroups().forEach(mockSuperGroup -> {
            try {
                this.superGroupRepository.create(new SuperGroup(
                        mockSuperGroup.id(),
                        mockSuperGroup.name(),
                        mockSuperGroup.prettyName(),
                        mockSuperGroup.type(),
                        new Email(mockSuperGroup.name() + "@chalmers.it"),
                        new Text()));
            } catch (SuperGroupService.SuperGroupNotFoundException e) {
                LOGGER.error("Error creating supergroup: " + mockSuperGroup.name() + "; Super group already exists, skipping...");
            }
        });

        LOGGER.info("Supergroups created");

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");
    }

}
