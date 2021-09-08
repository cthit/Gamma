package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.SuperGroupFacade;
import it.chalmers.gamma.domain.user.Name;
import it.chalmers.gamma.domain.common.PrettyName;
import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.domain.common.Text;
import it.chalmers.gamma.domain.common.Email;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroup;

import it.chalmers.gamma.app.supergroup.SuperGroupRepository;
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

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupAdminController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    private record CreateOrEditSuperGroupRequest(Name name,
                                                 PrettyName prettyName,
                                                 SuperGroupType type,
                                                 Email email,
                                                 Text description) { }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@RequestBody CreateOrEditSuperGroupRequest request) {
        try {
            this.superGroupFacade.createSuperGroup(
                    new SuperGroup(
                            SuperGroupId.generate(),
                            request.name,
                            request.prettyName,
                            request.type,
                            request.email,
                            request.description
                    )
            );
        } catch (SuperGroupRepository.SuperGroupAlreadyExistsException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new SuperGroupCreatedResponse();
    }

    @DeleteMapping("/{id}")
    public SuperGroupDeletedResponse removeSuperGroup(@PathVariable("id") SuperGroupId id) {
        try {
            this.superGroupFacade.deleteSuperGroup(id);
        } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
        return new SuperGroupDeletedResponse();
    }

    @PutMapping("/{id}")
    public SuperGroupUpdatedResponse updateSuperGroup(@PathVariable("id") SuperGroupId id,
                                                @RequestBody CreateOrEditSuperGroupRequest request) {
        try {
            this.superGroupFacade.updateSuperGroup(new SuperGroup(
                    id,
                    request.name,
                    request.prettyName,
                    request.type,
                    request.email,
                    request.description
            ));
        } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
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
