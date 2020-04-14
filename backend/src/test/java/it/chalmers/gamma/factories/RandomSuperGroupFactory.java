package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.utils.GenerationUtils;

public final class RandomSuperGroupFactory {
    public static FKITSuperGroupDTO generateSuperGroup(String groupName) {
        return new FKITSuperGroupDTO(
                groupName,
                groupName,
                GroupType.COMMITTEE,
                GenerationUtils.generateRandomString()
        );
    }
}
