package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.supergroup.exception.SuperGroupHasGroupsException;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.supergroup.request.CreateSuperGroupRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.group.controller.response.GroupDeletedResponse;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.controller.response.GroupEditedResponse;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse.GetSuperGroupResponseObject;
import it.chalmers.gamma.supergroup.response.RemoveSubGroupsBeforeRemovingSuperGroupResponse;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.supergroup.response.SuperGroupDoesNotExistResponse;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/superGroups")
public class SuperGroupAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperGroupAdminController.class);

    private final SuperGroupFinder superGroupFinder;
    private final SuperGroupService superGroupService;
    private final GroupService groupService;

    public SuperGroupAdminController(SuperGroupFinder superGroupFinder,
                                     SuperGroupService superGroupService,
                                     GroupService groupService) {
        this.superGroupFinder = superGroupFinder;
        this.superGroupService = superGroupService;
        this.groupService = groupService;
    }

    @PostMapping()
    public GetSuperGroupResponseObject createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                        BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        SuperGroupDTO group = this.superGroupService.createSuperGroup(requestToDTO(request));
        return new GetSuperGroupResponse(group).toResponseObject();
    }


    @DeleteMapping("/{id}")
    public GroupDeletedResponse removeSuperGroup(@PathVariable("id") UUID id) {
        try {
            this.superGroupService.removeGroup(id);
        } catch (SuperGroupNotFoundException e) {
            LOGGER.error("Super group not found", e);
            throw new SuperGroupDoesNotExistResponse();
        } catch (SuperGroupHasGroupsException e) {
            LOGGER.error("Can't delete super group when it has groups", e);
            throw new RemoveSubGroupsBeforeRemovingSuperGroupResponse();
        }
        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public GroupEditedResponse updateSuperGroup(@PathVariable("id") UUID id,
                                                @RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupService.updateSuperGroup(requestToDTO(request));
        } catch (SuperGroupNotFoundException | IDsNotMatchingException e) {
            LOGGER.error("Super group not found", e);
            throw new SuperGroupDoesNotExistResponse();
        }
        return new GroupEditedResponse();
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request, UUID id) {
        return new SuperGroupDTO(
                id,
                request.getName(),
                request.getPrettyName(),
                request.getType(),
                request.getEmail()
        );
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request) {
        return new SuperGroupDTO(
                request.getName(),
                request.getPrettyName(),
                request.getType(),
                request.getEmail());
    }

}
