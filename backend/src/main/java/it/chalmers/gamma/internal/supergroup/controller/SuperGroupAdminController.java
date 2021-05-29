package it.chalmers.gamma.internal.supergroup.controller;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.SuperGroupType;
import it.chalmers.gamma.internal.text.service.TextDTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
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

    private record CreateOrEditSuperGroupRequest(EntityName name,
                                                 PrettyName prettyName,
                                                 SuperGroupType type,
                                                 Email email,
                                                 TextDTO description) { }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@Valid @RequestBody CreateOrEditSuperGroupRequest request) {
        try {
            this.superGroupService.create(new SuperGroupDTO(
                    null,
                    request.name,
                    request.prettyName,
                    request.type,
                    request.email,
                    request.description
            ));
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
                                                @RequestBody CreateOrEditSuperGroupRequest request) {
        try {
            this.superGroupService.update(new SuperGroupDTO(
                    id,
                    request.name,
                    request.prettyName,
                    request.type,
                    request.email,
                    request.description
            ));
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new SuperGroupUpdatedResponse();
    }

    private static class SuperGroupUpdatedResponse extends SuccessResponse { }

    private static class SuperGroupDeletedResponse extends SuccessResponse {}

    private static class SuperGroupCreatedResponse extends SuccessResponse { }

    private static class SuperGroupAlreadyExistsResponse extends ErrorResponse {
        private SuperGroupAlreadyExistsResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class SuperGroupDoesNotExistResponse extends ErrorResponse {
        private SuperGroupDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }
}
