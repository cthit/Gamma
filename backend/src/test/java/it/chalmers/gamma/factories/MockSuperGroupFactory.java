package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupService;
import it.chalmers.gamma.utils.GenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockSuperGroupFactory {

    @Autowired
    private SuperGroupService superGroupService;

    public SuperGroupDTO generateSuperGroup(String groupName) {
        return new SuperGroupDTO(
                groupName,
                groupName,
                GroupType.COMMITTEE,
                GenerationUtils.generateRandomString()
        );
    }

    public SuperGroupDTO saveSuperGroup(SuperGroupDTO superGroup) {
        return this.superGroupService.createSuperGroup(superGroup);
    }
}
