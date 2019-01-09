package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.requests.CreateSuperGroupRequest;
import it.chalmers.gamma.response.FKITSuperGroupCreatedResponse;
import it.chalmers.gamma.response.GetGroupsResponse;
import it.chalmers.gamma.response.GetSuperGroupResponse;
import it.chalmers.gamma.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.response.GroupDeletedResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.GroupEditedResponse;
import it.chalmers.gamma.response.GroupsResponse;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.List;
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
    private final FKITService fkitService;


    public SuperGroupAdminController(FKITSuperGroupService fkitSuperGroupService, FKITService fkitService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitService = fkitService;
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
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FKITSuperGroup>> getAllSuperGroups() {
        return new GetGroupsResponse(this.fkitSuperGroupService.getAllGroups());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FKITSuperGroup> getSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        return new GetSuperGroupResponse(this.fkitSuperGroupService.getGroup(UUID.fromString(id)));
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

    @RequestMapping(value = "/{id}/subgroups", method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getAllSubGroups(@PathVariable("id") String id){
        FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(UUID.fromString(id));
        if(superGroup == null){
            throw new GroupDoesNotExistResponse();
        }
        List<FKITGroup> groups = this.fkitService.getGroupsInSuperGroup(superGroup);
        return new GroupsResponse(groups);
    }
}
