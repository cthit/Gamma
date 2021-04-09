package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupService;
import it.chalmers.gamma.domain.group.controller.GroupDeletedResponse;
import it.chalmers.gamma.domain.group.controller.GroupUpdatedResponse;

import javax.validation.Valid;

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

    private final SuperGroupService superGroupService;

    public SuperGroupAdminController(SuperGroupService superGroupService) {
        this.superGroupService = superGroupService;
    }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@Valid @RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupService.create(requestToDTO(request));
        } catch (EntityAlreadyExistsException e) {
            throw new SuperGroupAlreadyExistsResponse();
        }
        return new SuperGroupCreatedResponse();
    }


    @DeleteMapping("/{id}")
    public SuperGroupDeletedResponse removeSuperGroup(@PathVariable("id") SuperGroupId id) {
        this.superGroupService.delete(id);
        return new SuperGroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public SuperGroupUpdatedResponse updateSuperGroup(@PathVariable("id") SuperGroupId id,
                                                 @RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupService.update(requestToDTO(request, id));
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new SuperGroupUpdatedResponse();
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request, SuperGroupId id) {
        return new SuperGroupDTO(
                id,
                request.name,
                request.prettyName,
                request.type,
                request.email,
                request.description
        );
    }

    private SuperGroupDTO requestToDTO(CreateSuperGroupRequest request) {
        return this.requestToDTO(request, null);
    }

}
