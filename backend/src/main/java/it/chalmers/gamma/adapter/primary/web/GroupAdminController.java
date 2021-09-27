package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.facade.GroupFacade;
import it.chalmers.gamma.app.port.repository.GroupRepository;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/groups")
public final class GroupAdminController {

    private final GroupFacade groupFacade;

    public GroupAdminController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record CreateOrEditGroupRequest(String name,
                                            String prettyName,
                                            UUID superGroup,
                                            String email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@RequestBody CreateOrEditGroupRequest request) {
        this.groupFacade.createGroup(
                new GroupFacade.NewGroup(
                        request.name,
                        request.prettyName,
                        request.superGroup,
                        request.email
                )
        );
        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@RequestBody CreateOrEditGroupRequest request,
                                          @PathVariable("id") UUID id) {
        this.groupFacade.updateGroup(
                new GroupFacade.UpdateGroup(
                        id,
                        request.name,
                        request.prettyName,
                        request.superGroup,
                        request.email
                )
        );
        return new GroupUpdatedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") UUID id) {
        try {
            this.groupFacade.delete(id);
            return new GroupDeletedResponse();
        } catch (GroupRepository.GroupNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
    }

    private static class GroupCreatedResponse extends SuccessResponse { }

    private static class GroupDeletedResponse extends SuccessResponse { }

    private static class GroupUpdatedResponse extends SuccessResponse { }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

    private static class GroupAlreadyExistsResponse extends AlreadyExistsResponse { }

}
