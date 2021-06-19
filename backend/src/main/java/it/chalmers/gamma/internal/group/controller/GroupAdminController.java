package it.chalmers.gamma.internal.group.controller;

import it.chalmers.gamma.domain.EntityName;
import it.chalmers.gamma.domain.PrettyName;
import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.GroupWithMembers;
import it.chalmers.gamma.domain.UserPost;
import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.group.service.GroupShallowDTO;
import it.chalmers.gamma.domain.Membership;
import it.chalmers.gamma.internal.membership.service.MembershipService;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.AlreadyExistsResponse;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/admin/groups")
public final class GroupAdminController {

    private final GroupService groupService;
    private final MembershipService membershipService;

    public GroupAdminController(GroupService groupService,
                                MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    private record CreateOrEditGroupRequest(EntityName name,
                                            PrettyName prettyName,
                                            SuperGroupId superGroup,
                                            Email email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@RequestBody CreateOrEditGroupRequest request) {
        try {
            this.groupService.create(
                    new GroupShallowDTO(
                            GroupId.generate(),
                            request.email,
                            request.name,
                            request.prettyName,
                            request.superGroup
                    )
            );
        } catch (GroupService.GroupAlreadyExistsException e) {
            throw new GroupAlreadyExistsResponse();
        }

        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@RequestBody CreateOrEditGroupRequest request,
                                          @PathVariable("id") GroupId id) {
        try {
            GroupShallowDTO group = new GroupShallowDTO(
                    id,
                    request.email,
                    request.name,
                    request.prettyName,
                    request.superGroup
            );
            this.groupService.update(group);
        } catch (GroupService.GroupNotFoundException e) {
            throw new GroupNotFoundResponse();
        }

        return new GroupUpdatedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") GroupId id) {
        try {
            this.groupService.delete(id);
        } catch (GroupService.GroupNotFoundException e) {
            throw new GroupNotFoundResponse();
        }

        return new GroupDeletedResponse();
    }

    /**
     * This is the only thing a non-admin user that's part of the group can change
     */
    @PutMapping("/{id}/avatar")
    public GroupUpdatedResponse editAvatar(@PathVariable("id") GroupId id, @RequestParam MultipartFile file) {
/*
        try {
            String url = ImageUtils.saveImage(file, file.getName());
            GroupDTO group = this.groupFinder.get(id);
            this.groupService.update(
                    new GroupShallowDTO.GroupDTOBuilder()
                            .from(group)
                            .avatarUrl(url)
                            .build()
            );
        } catch (FileNotFoundResponse e) {
            throw new FileNotSavedException();
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
*/
        return new GroupUpdatedResponse();
    }

    private GroupWithMembers toGroupWithMembers(Group group) {
        return new GroupWithMembers(
                group,
                toUserPosts(this.membershipService.getMembershipsByGroup(group.id()))
        );
    }

    private List<UserPost> toUserPosts(List<Membership> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(membership.user(), membership.post()))
                .collect(Collectors.toList());
    }

    private static class GroupCreatedResponse extends SuccessResponse { }

    private static class GroupDeletedResponse extends SuccessResponse { }

    private static class GroupUpdatedResponse extends SuccessResponse { }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

    private static class GroupAlreadyExistsResponse extends AlreadyExistsResponse { }

}
