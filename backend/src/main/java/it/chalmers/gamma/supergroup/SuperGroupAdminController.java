package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.supergroup.request.CreateSuperGroupRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.group.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.group.response.GroupDeletedResponse;
import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.response.GroupEditedResponse;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse.GetSuperGroupResponseObject;
import it.chalmers.gamma.supergroup.response.RemoveSubGroupsBeforeRemovingSuperGroupResponse;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/superGroups")       // What should this URL be?
public class SuperGroupAdminController {
    private final SuperGroupService superGroupService;
    private final GroupService groupService;


    public SuperGroupAdminController(SuperGroupService superGroupService, GroupService groupService) {
        this.superGroupService = superGroupService;
        this.groupService = groupService;
    }

    @PostMapping()
    public GetSuperGroupResponseObject createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                                              BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.superGroupService.groupExists(request.getName())) {
            throw new GroupAlreadyExistsResponse();
        }
        SuperGroupDTO group = this.superGroupService.createSuperGroup(requestToDTO(request));
        return new GetSuperGroupResponse(group).toResponseObject();
    }



    @DeleteMapping("/{id}")
    public GroupDeletedResponse removeSuperGroup(@PathVariable("id") String id) {
        if (!this.superGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        SuperGroupDTO superGroup = this.superGroupService.getGroupDTO(id);
        if (!this.groupService.getAllGroupsWithSuperGroup(superGroup).isEmpty()) {
            throw new RemoveSubGroupsBeforeRemovingSuperGroupResponse();
        }
        this.superGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public GroupEditedResponse updateSuperGroup(@PathVariable("id") String id,
                                                   @RequestBody CreateSuperGroupRequest request) {
        if (!this.superGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        this.superGroupService.updateSuperGroup(UUID.fromString(id), requestToDTO(request));
        return new GroupEditedResponse();
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request) {
        return new SuperGroupDTO(
                request.getName(),
                request.getPrettyName(),
                request.getType(),
                request.getEmail());
    }

}
