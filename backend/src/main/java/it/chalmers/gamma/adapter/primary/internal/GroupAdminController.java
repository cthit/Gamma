package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.group.GroupFacade;

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
                                      UUID superGroup) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@RequestBody CreateGroupRequest request) {
        try {
            this.groupFacade.create(
                    new GroupFacade.NewGroup(
                            request.name,
                            request.prettyName,
                            request.superGroup
                    )
            );
        } catch (GroupFacade.SuperGroupNotFoundRuntimeException e) {
            throw new SuperGroupNotFoundResponse();
        } catch (GroupFacade.GroupAlreadyExistsException e) {
            throw new GroupAlreadyExistsResponse();
        }
        return new GroupCreatedResponse();
    }

    private record EditGroupRequest(int version,
                                    String name,
                                    String prettyName,
                                    UUID superGroup) { }


    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@RequestBody EditGroupRequest request,
                                          @PathVariable("id") UUID id) {
        try {
            this.groupFacade.update(
                    new GroupFacade.UpdateGroup(
                            id,
                            request.version,
                            request.name,
                            request.prettyName,
                            request.superGroup
                    )
            );
        } catch (GroupFacade.GroupAlreadyExistsException e) {
            throw new GroupAlreadyExistsResponse();
        }
        return new GroupUpdatedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") UUID id) {
        try {
            this.groupFacade.delete(id);
            return new GroupDeletedResponse();
        } catch (GroupFacade.GroupNotFoundRuntimeException e) {
            throw new GroupNotFoundResponse();
        }
    }

    private static class GroupCreatedResponse extends SuccessResponse { }

    private static class GroupDeletedResponse extends SuccessResponse { }

    private static class GroupUpdatedResponse extends SuccessResponse { }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

    private static class SuperGroupNotFoundResponse extends NotFoundResponse { }

    private static class GroupAlreadyExistsResponse extends AlreadyExistsResponse { }

}
