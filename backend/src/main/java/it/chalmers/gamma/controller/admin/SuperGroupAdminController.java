package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.group.GroupAlreadyExistsResponse;
import it.chalmers.gamma.response.group.GroupDeletedResponse;
import it.chalmers.gamma.response.group.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.group.GroupEditedResponse;
import it.chalmers.gamma.response.supergroup.GetSuperGroupResponse;
import it.chalmers.gamma.response.supergroup.GetSuperGroupResponse.GetSuperGroupResponseObject;
import it.chalmers.gamma.response.supergroup.RemoveSubGroupsBeforeRemovingSuperGroupResponse;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
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
    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITGroupService fkitGroupService;


    public SuperGroupAdminController(FKITSuperGroupService fkitSuperGroupService, FKITGroupService fkitGroupService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupService = fkitGroupService;
    }

    @PostMapping()
    public GetSuperGroupResponseObject createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                                              BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.fkitSuperGroupService.groupExists(request.getName())) {
            throw new GroupAlreadyExistsResponse();
        }
        FKITSuperGroupDTO group = this.fkitSuperGroupService.createSuperGroup(requestToDTO(request));
        return new GetSuperGroupResponse(group).toResponseObject();
    }



    @DeleteMapping("/{id}")
    public GroupDeletedResponse removeSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(id);
        if (!this.fkitGroupService.getAllGroupsWithSuperGroup(superGroup).isEmpty()) {
            throw new RemoveSubGroupsBeforeRemovingSuperGroupResponse();
        }
        this.fkitSuperGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public GroupEditedResponse updateSuperGroup(@PathVariable("id") String id,
                                                   @RequestBody CreateSuperGroupRequest request) {
        if (!this.fkitSuperGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        this.fkitSuperGroupService.updateSuperGroup(UUID.fromString(id), requestToDTO(request));
        return new GroupEditedResponse();
    }

    private FKITSuperGroupDTO requestToDTO(CreateSuperGroupRequest request) {
        return new FKITSuperGroupDTO(
                request.getName(),
                request.getPrettyName(),
                request.getType(),
                request.getEmail());
    }

}
