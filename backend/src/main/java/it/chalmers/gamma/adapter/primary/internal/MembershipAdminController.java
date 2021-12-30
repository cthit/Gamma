package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.group.GroupFacade;

import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/admin/groups")
public final class MembershipAdminController {

    private final GroupFacade groupFacade;

    public MembershipAdminController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    private record AddUserGroupRequest(UUID userId, UUID postId, String unofficialName) { }

    private record Member(UUID userId, UUID postId, String unofficialPostName) {
    }

    private record EditMembers() { }

    @PutMapping("/{groupId}/members")
    public EditedMembershipResponse editMembers(@PathVariable("groupId") UUID groupId,
                                                    @RequestBody List<Member> members) {
        try {
            this.groupFacade.setMembers(groupId, members.stream().map(member -> new GroupFacade.ShallowMember(
                    member.userId, member.postId, member.unofficialPostName
            )).toList());
        } catch (GroupFacade.GroupNotFoundRuntimeException e) {
            throw new GroupNotFoundResponse();
        }

        return new EditedMembershipResponse();
    }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

    private static class EditedMembershipResponse extends SuccessResponse { }

    private static class PostNotFoundResponse extends NotFoundResponse { }

    private static class UserNotFoundResponse extends NotFoundResponse { }
}
