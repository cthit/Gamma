package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.response.super_group.FKITSuperGroupCreatedResponse;
import it.chalmers.gamma.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.response.GroupDeletedResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.GroupEditedResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.super_group.GetSuperGroupResponse;
import it.chalmers.gamma.response.super_group.GetSuperGroupResponse.GetSuperGroupResponseObject;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/superGroups")       // What should this URL be?
public class SuperGroupAdminController {
    private final FKITSuperGroupService fkitSuperGroupService;


    public SuperGroupAdminController(FKITSuperGroupService fkitSuperGroupService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public GetSuperGroupResponseObject createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                                              BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.fkitSuperGroupService.groupExists(request.getName())) {
            throw new GroupAlreadyExistsResponse();
        }
        FKITSuperGroupDTO group = this.fkitSuperGroupService.createSuperGroup(requestToDTO(request));
        return new GetSuperGroupResponse(group).getResponseObject();
    }



    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        this.fkitSuperGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateSuperGroup(@PathVariable("id") String id,
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
