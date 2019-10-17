package it.chalmers.delta.controller.admin;

import it.chalmers.delta.db.entity.FKITSuperGroup;
import it.chalmers.delta.requests.CreateSuperGroupRequest;
import it.chalmers.delta.response.FKITSuperGroupCreatedResponse;
import it.chalmers.delta.response.GroupAlreadyExistsResponse;
import it.chalmers.delta.response.GroupDeletedResponse;
import it.chalmers.delta.response.GroupDoesNotExistResponse;
import it.chalmers.delta.response.GroupEditedResponse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.service.FKITSuperGroupService;
import it.chalmers.delta.util.InputValidationUtils;

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
    public ResponseEntity<FKITSuperGroup> createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.fkitSuperGroupService.groupExists(request.getName())) {
            throw new GroupAlreadyExistsResponse();
        }
        FKITSuperGroup group = this.fkitSuperGroupService.createSuperGroup(request);
        return new FKITSuperGroupCreatedResponse(group);
    }



    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> removeSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        this.fkitSuperGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateSuperGroup(@PathVariable("id") String id,
                                                   @RequestBody CreateSuperGroupRequest request) {
        if (!this.fkitSuperGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        this.fkitSuperGroupService.updateSuperGroup(UUID.fromString(id), request);
        return new GroupEditedResponse();
    }

}
