package it.chalmers.gamma.factories;

import it.chalmers.gamma.domain.text.Text;
import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.supergroup.SuperGroupDTO;
import it.chalmers.gamma.group.controller.request.CreateOrEditGroupRequest;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.utils.CharacterTypes;
import it.chalmers.gamma.utils.GenerationUtils;
import java.util.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockFKITGroupFactory {

    @Autowired
    private GroupService groupService;

    public GroupDTO generateActiveFKITGroup(String groupName, SuperGroupDTO superGroupDTO) {
        Calendar current = Calendar.getInstance();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        return new GroupDTO.GroupDTOBuilder().id(current).becomesActive(nextYear).becomesInactive(new Text()).description(GenerationUtils.generateRandomString()).email(new Text()).function(groupName).name(groupName).prettyName(GenerationUtils.generateRandomString()).avatarUrl(superGroupDTO).build();
    }

    public GroupDTO saveGroup(GroupDTO group) {
        return this.groupService.createGroup(group);
    }

    public CreateOrEditGroupRequest createValidRequest() {
        CreateOrEditGroupRequest request = new CreateOrEditGroupRequest();
        request.setName(GenerationUtils.generateRandomString(30, CharacterTypes.LOWERCASE));
        request.setPrettyName(GenerationUtils.generateRandomString(30, CharacterTypes.LOWERCASE));
        request.setFunction(GenerationUtils.generateText());
        request.setBecomesActive(Calendar.getInstance());
        request.setBecomesInactive(Calendar.getInstance());
        return request;
    }
}
