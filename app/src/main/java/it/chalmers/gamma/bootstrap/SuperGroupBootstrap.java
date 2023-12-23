package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.supergroup.domain.*;
import it.chalmers.gamma.app.user.domain.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SuperGroupBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperGroupBootstrap.class);

    private final MockData mockData;
    private final SuperGroupTypeRepository superGroupTypeRepository;
    private final SuperGroupRepository superGroupRepository;
    private final BootstrapSettings bootstrapSettings;

    public SuperGroupBootstrap(MockData mockData,
                               SuperGroupTypeRepository superGroupTypeRepository,
                               SuperGroupRepository superGroupRepository,
                               BootstrapSettings bootstrapSettings) {
        this.mockData = mockData;
        this.superGroupTypeRepository = superGroupTypeRepository;
        this.superGroupRepository = superGroupRepository;
        this.bootstrapSettings = bootstrapSettings;
    }

    public void createSuperGroups() {
        if (!this.bootstrapSettings.mocking() || !this.superGroupRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");

        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).distinct().forEach(type -> {
            try {
                this.superGroupTypeRepository.add(new SuperGroupType(type));
            } catch (SuperGroupTypeRepository.SuperGroupTypeAlreadyExistsException e) {
                LOGGER.error("Error creating supergroup type: " + type + ";");
            }
        });

        LOGGER.info("Supergroup types created");

        mockData.superGroups().forEach(mockSuperGroup -> {
            this.superGroupRepository.save(new SuperGroup(
                    new SuperGroupId(mockSuperGroup.id()),
                    0,
                    new Name(mockSuperGroup.name()),
                    new PrettyName(mockSuperGroup.prettyName()),
                    new SuperGroupType(mockSuperGroup.type()),
                    new Text())
            );
            // LOGGER.error("Error creating supergroup: " + mockSuperGroup.name() + "; Super group already exists, skipping...");
        });

        LOGGER.info("Supergroups created");

        LOGGER.info("==========                      ==========");
    }

}
