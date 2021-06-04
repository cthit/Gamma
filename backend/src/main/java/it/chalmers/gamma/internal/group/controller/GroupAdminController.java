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
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipService;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
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


    @GetMapping()
    public List<GroupWithMembers> getGroups() {
        return this.groupService.getAll()
                .stream()
                .map(this::toGroupWithMembers)
                .collect(Collectors.toList());
    }

    private record CreateOrEditGroupRequest(EntityName name,
                                            PrettyName prettyName,
                                            SuperGroupId superGroup,
                                            Email email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateOrEditGroupRequest request) {
        try {
            this.groupService.create(new GroupShallowDTO(
                    null,
                    request.email,
                    request.name,
                    request.prettyName,
                    request.superGroup
            ));
        } catch (GroupService.GroupAlreadyExistsException e) {
            throw new GroupAlreadyExistsResponse();
        }

        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@Valid @RequestBody CreateOrEditGroupRequest request,
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

    private List<UserPost> toUserPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(membership.user(), membership.post()))
                .collect(Collectors.toList());
    }

    private static class GroupAlreadyExistsResponse extends ErrorResponse {
        private GroupAlreadyExistsResponse() {
            super(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private static class GroupCreatedResponse extends SuccessResponse {

    }

    private static class GroupDeletedResponse extends SuccessResponse {

    }

    private static class GroupNotFoundResponse extends ErrorResponse {
        private GroupNotFoundResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

    private static class GroupUpdatedResponse extends SuccessResponse {

    }



}
