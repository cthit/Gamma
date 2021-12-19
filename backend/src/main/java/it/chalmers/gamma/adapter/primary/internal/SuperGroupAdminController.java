package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.supergroup.domain.SuperGroupId;

import it.chalmers.gamma.app.supergroup.domain.SuperGroupRepository;
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

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/superGroups")
public class SuperGroupAdminController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupAdminController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    private record CreateSuperGroupRequest(String name,
                                           String prettyName,
                                           String type,
                                           String email,
                                           String svText,
                                           String enText) { }

    @PostMapping()
    public SuperGroupCreatedResponse createSuperGroup(@RequestBody CreateSuperGroupRequest request) {
        try {
            this.superGroupFacade.createSuperGroup(
                    new SuperGroupFacade.NewSuperGroup(
                            request.name,
                            request.prettyName,
                            request.type,
                            request.email,
                            request.svText,
                            request.enText
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

    private record EditSuperGroupRequest(int version,
                                         String name,
                                         String prettyName,
                                         String type,
                                         String email,
                                         String svText,
                                         String enText) { }

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
                            request.email,
                            request.svText,
                            request.enText
                    )
            );
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
