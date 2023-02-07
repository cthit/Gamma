package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.BadRequestResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/superGroups")
public class SuperGroupAdminController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupAdminController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupFacade.createSuperGroup(
                    new SuperGroupFacade.NewSuperGroup(
                            request.name,
                            request.prettyName,
                            request.type,
                            request.svDescription,
                            request.enDescription
                    )
            );
        } catch (SuperGroupRepository.SuperGroupAlreadyExistsException e) {
            throw new SuperGroupDoesNotFoundResponse();
        }
        return new SuperGroupCreatedResponse();
    }

    @DeleteMapping("/{id}")
    public SuperGroupDeletedResponse removeSuperGroup(@PathVariable("id") SuperGroupId id) {
        try {
            this.superGroupFacade.deleteSuperGroup(id);
            return new SuperGroupDeletedResponse();
        } catch (SuperGroupFacade.SuperGroupIsUsedException e) {
            throw new SuperGroupIsUsedResponse();
        } catch (SuperGroupFacade.SuperGroupNotFoundException e) {
            throw new SuperGroupDoesNotFoundResponse();
        }
    }

    @PutMapping("/{id}")
    public SuperGroupUpdatedResponse updateSuperGroup(@PathVariable("id") UUID id,
                                                      @RequestBody EditSuperGroupRequest request) {
        try {
            this.superGroupFacade.updateSuperGroup(
                    new SuperGroupFacade.UpdateSuperGroup(
                            id,
                            request.version,
                            request.name,
                            request.prettyName,
                            request.type,
                            request.svDescription,
                            request.enDescription
                    )
            );
        } catch (SuperGroupRepository.SuperGroupNotFoundException e) {
            throw new SuperGroupDoesNotFoundResponse();
        }
        return new SuperGroupUpdatedResponse();
    }

    private record CreateSuperGroupRequest(String name,
                                           String prettyName,
                                           String type,
                                           String svDescription,
                                           String enDescription) {
    }

    private record EditSuperGroupRequest(int version,
                                         String name,
                                         String prettyName,
                                         String type,
                                         String svDescription,
                                         String enDescription) {
    }

    private static class SuperGroupUpdatedResponse extends SuccessResponse {
    }

    private static class SuperGroupDeletedResponse extends SuccessResponse {
    }

    private static class SuperGroupCreatedResponse extends SuccessResponse {
    }

    private static class SuperGroupAlreadyExistsResponse extends AlreadyExistsResponse {
    }

    private static class SuperGroupDoesNotFoundResponse extends NotFoundResponse {
    }

    private static class SuperGroupIsUsedResponse extends BadRequestResponse {
    }

}
