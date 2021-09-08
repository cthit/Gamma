package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.GroupFacade;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.post.PostId;
import it.chalmers.gamma.domain.user.UserId;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/admin/groups")
public final class MembershipAdminController {

    private final GroupFacade groupFacade;

    public MembershipAdminController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record AddUserGroupRequest(UserId userId, PostId postId, String unofficialName) { }

    @PostMapping("/{id}/members")
    public MemberAddedToGroupResponse addUserToGroup(
            @RequestBody AddUserGroupRequest request,
            @PathVariable("id") GroupId groupId) {
//        this.membershipService.create(
//                new MembershipShallowDTO(
//                        request.postId,
//                        groupId,
//                        request.unofficialName,
//                        request.userId
//                )
//        );

        return new MemberAddedToGroupResponse();
    }

    @DeleteMapping("/{groupId}/members")
    public MemberRemovedFromGroupResponse deleteUserFromGroup(@PathVariable("groupId") GroupId groupId,
                                                              @RequestParam("userId") UserId userId,
                                                              @RequestParam("postId") PostId postId) {
//        this.membershipService.delete(new MembershipPK(postId, groupId, userId));
        return new MemberRemovedFromGroupResponse();
    }

    private record EditMembershipRequest(String unofficialName) { }

    @PutMapping("/{groupId}/members")
    public EditedMembershipResponse editUserInGroup(@PathVariable("groupId") GroupId groupId,
                                                    @RequestParam("userId") UserId userId,
                                                    @RequestParam("postId") PostId postId,
                                                    @RequestBody EditMembershipRequest request) {
//        try {
//            this.membershipService.update(
//                    new MembershipShallowDTO(
//                            postId,
//                            groupId,
//                            request.unofficialName,
//                            userId
//                    )
//            );
//        } catch (MembershipService.MembershipNotFoundException e) {
//            e.printStackTrace();
//        }

        return new EditedMembershipResponse();
    }

    private static class EditedMembershipResponse extends SuccessResponse { }

    private static class MemberAddedToGroupResponse extends SuccessResponse { }

    private static class MemberRemovedFromGroupResponse extends SuccessResponse { }

    private static class MembershipNotFoundResponse extends NotFoundResponse { }
}
