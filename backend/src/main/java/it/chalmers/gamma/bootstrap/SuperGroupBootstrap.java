package it.chalmers.gamma.bootstrap;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.internal.supergroup.type.service.SuperGroupTypeService;
import it.chalmers.gamma.internal.text.service.TextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public SuperGroupBootstrap(MockData mockData,
                               SuperGroupTypeService superGroupTypeService,
                               SuperGroupService superGroupService) {
        this.mockData = mockData;
        this.superGroupTypeService = superGroupTypeService;
        this.superGroupService = superGroupService;
    }

    @PostConstruct
    public void createSuperGroups() {
        mockData.superGroups().stream().map(MockData.MockSuperGroup::type).forEach(type -> {
            try {
                this.superGroupTypeService.create(type);
            } catch (SuperGroupTypeService.SuperGroupNotFoundException ignored) { }
        });

        mockData.superGroups().forEach(mockSuperGroup -> {
            try {
                this.superGroupService.create(new SuperGroupDTO(
                        mockSuperGroup.id(),
                        mockSuperGroup.name(),
                        mockSuperGroup.prettyName(),
                        mockSuperGroup.type(),
                        new Email(mockSuperGroup.name() + "@chalmers.it"),
                        new TextDTO("", "")));
            } catch (SuperGroupService.SuperGroupNotFoundException e) {
                LOGGER.error("Error creating supergroup: " + mockSuperGroup.name() + "; Super group already exists, skipping...");
            }
        });

        LOGGER.info("Super groups created");
    }

}
