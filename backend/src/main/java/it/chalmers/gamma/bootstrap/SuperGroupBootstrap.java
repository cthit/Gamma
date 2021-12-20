package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupTypeRepository;
import it.chalmers.gamma.app.common.Email;
import it.chalmers.gamma.app.common.PrettyName;
import it.chalmers.gamma.app.common.Text;
import it.chalmers.gamma.app.supergroup.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupType;
import it.chalmers.gamma.app.user.domain.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void createSuperGroups() {
        if (!this.mocking || !this.superGroupRepository.getAll().isEmpty()) {
            return;
        }

        LOGGER.info("========== SUPERGROUP BOOTSTRAP ==========");

        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).forEach(type -> {
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
