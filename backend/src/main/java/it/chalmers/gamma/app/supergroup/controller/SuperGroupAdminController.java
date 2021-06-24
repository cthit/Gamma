package it.chalmers.gamma.app.supergroup.controller;

import it.chalmers.gamma.app.domain.EntityName;
import it.chalmers.gamma.app.domain.PrettyName;
import it.chalmers.gamma.app.domain.SuperGroupType;
import it.chalmers.gamma.app.domain.Text;
import it.chalmers.gamma.app.domain.Email;
import it.chalmers.gamma.app.domain.SuperGroupId;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.supergroup.service.SuperGroupService;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/superGroups")
public class SuperGroupAdminController {

    private final SuperGroupService superGroupService;

    public SuperGroupAdminController(SuperGroupService superGroupService) {
        this.superGroupService = superGroupService;
    }

    private record CreateOrEditSuperGroupRequest(EntityName name,
                                                 PrettyName prettyName,
                                                 SuperGroupType type,
                                                 Email email,
                                                 Text description) { }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@RequestBody CreateOrEditSuperGroupRequest request) {
        try {
            this.superGroupService.create(new SuperGroup(
                    null,
                    request.name,
                    request.prettyName,
                    request.type,
                    request.email,
                    request.description
            ));
        } catch (SuperGroupService.SuperGroupNotFoundException e) {
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
            this.superGroupService.update(new SuperGroup(
                    id,
                    request.name,
                    request.prettyName,
                    request.type,
                    request.email,
                    request.description
            ));
        } catch (SuperGroupService.SuperGroupNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new SuperGroupUpdatedResponse();
    }

    private static class SuperGroupUpdatedResponse extends SuccessResponse { }

    private static class SuperGroupDeletedResponse extends SuccessResponse {}

    private static class SuperGroupCreatedResponse extends SuccessResponse { }

    private static class SuperGroupAlreadyExistsResponse extends AlreadyExistsResponse { }

    private static class SuperGroupDoesNotExistResponse extends NotFoundResponse { }
}
