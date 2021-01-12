package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.group.FKITGroupDTO;
import it.chalmers.gamma.supergroup.FKITSuperGroupDTO;
import it.chalmers.gamma.group.request.CreateGroupRequest;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockFKITGroupFactory {

    @Autowired
    private GroupService groupService;

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

    public CreateGroupRequest createValidRequest() {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName(GenerationUtils.generateRandomString(30, CharacterTypes.LOWERCASE));
        request.setPrettyName(GenerationUtils.generateRandomString(30, CharacterTypes.LOWERCASE));
        request.setFunction(GenerationUtils.generateText());
        request.setBecomesActive(Calendar.getInstance());
        request.setBecomesInactive(Calendar.getInstance());
        return request;
    }
}
