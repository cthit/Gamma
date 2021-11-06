package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.facade.internal.GroupFacade;
import it.chalmers.gamma.app.repository.GroupRepository;

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

    private record CreateGroupRequest(String name,
                                      String prettyName,
                                      UUID superGroup,
                                      String email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@RequestBody CreateGroupRequest request) {
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

    private record EditGroupRequest(int version,
                                    String name,
                                    String prettyName,
                                    UUID superGroup,
                                    String email) { }


    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@RequestBody EditGroupRequest request,
                                          @PathVariable("id") UUID id) {
        this.groupFacade.updateGroup(
                new GroupFacade.UpdateGroup(
                        id,
                        request.version,
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
