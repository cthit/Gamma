package it.chalmers.gamma.factories;

import it.chalmers.gamma.db.entity.Text;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockFKITGroupFactory {

    @Autowired
    private FKITGroupService groupService;

    public FKITGroupDTO generateActiveFKITGroup(String groupName, FKITSuperGroupDTO superGroupDTO) {
        Calendar current = Calendar.getInstance();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        return new FKITGroupDTO(
                current,
                nextYear,
                new Text(),
                GenerationUtils.generateRandomString(),
                new Text(),
                groupName,
                groupName,
                GenerationUtils.generateRandomString(),
                superGroupDTO
                );
    }

    public FKITGroupDTO saveGroup(FKITGroupDTO group) {
        return this.groupService.createGroup(group);
    }
}
