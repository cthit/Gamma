package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.domain.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.controller.response.*;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupService;
import it.chalmers.gamma.domain.supergroup.service.exception.SuperGroupHasGroupsException;
import it.chalmers.gamma.domain.supergroup.controller.request.CreateSuperGroupRequest;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.domain.group.controller.response.GroupDeletedResponse;
import it.chalmers.gamma.domain.group.controller.response.GroupUpdatedResponse;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.util.InputValidationUtils;

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
    public SuperGroupCreatedResponse createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request,
                                                      BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        try {
            this.superGroupService.create(requestToDTO(request));
        } catch (EntityAlreadyExistsException e) {
            throw new SuperGroupAlreadyExistsResponse();
        }
        return new SuperGroupCreatedResponse();
    }


    @DeleteMapping("/{id}")
    public GroupDeletedResponse removeSuperGroup(@PathVariable("id") SuperGroupId id) {
        this.superGroupService.delete(id);
        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse updateSuperGroup(@PathVariable("id") SuperGroupId id,
                                                 @RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupService.update(requestToDTO(request, id));
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new GroupUpdatedResponse();
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request, SuperGroupId id) {
        return new SuperGroupDTO(
                id,
                request.getName(),
                request.getPrettyName(),
                request.getType(),
                request.getEmail(),
                request.getDescription()
        );
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request) {
        return this.requestToDTO(request, null);
    }

}