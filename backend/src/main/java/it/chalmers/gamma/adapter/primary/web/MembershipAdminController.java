package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.facade.GroupFacade;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/groups")
public final class MembershipAdminController {

    private final GroupFacade groupFacade;

    public MembershipAdminController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record AddUserGroupRequest(UUID userId, UUID postId, String unofficialName) { }

    @PostMapping("/{id}/members")
    public MemberAddedToGroupResponse addUserToGroup(
            @RequestBody AddUserGroupRequest request,
            @PathVariable("id") UUID groupId) {

        this.groupFacade.addMember(groupId, request.userId, request.postId, request.unofficialName);
        return new MemberAddedToGroupResponse();
    }

    @DeleteMapping("/{groupId}/members")
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("groupId") UUID groupId,
                                                              @RequestParam("userId") UUID userId,
                                                              @RequestParam("postId") UUID postId) {
        this.groupFacade.removeMember(groupId, userId, postId);
        return new MemberRemovedFromGroupResponse();
    }

    private record EditMembershipRequest(String unofficialName) { }

    @PutMapping("/{groupId}/members")
    public EditedMembershipResponse editUserInGroup(@PathVariable("groupId") UUID groupId,
                                                    @RequestParam("userId") UUID userId,
                                                    @RequestParam("postId") UUID postId,
                                                    @RequestBody EditMembershipRequest request) {
        this.groupFacade.updateMember(
                groupId,
                userId,
                postId,
                new GroupFacade.UpdateMember(
                        request.unofficialName
                )
        );
        return new EditedMembershipResponse();
    }

    private static class EditedMembershipResponse extends SuccessResponse { }

    private static class MemberAddedToGroupResponse extends SuccessResponse { }

    private static class MemberRemovedFromGroupResponse extends SuccessResponse { }

    private static class MembershipNotFoundResponse extends NotFoundResponse { }
}
