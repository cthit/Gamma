package it.chalmers.gamma.internal.group.controller;

import it.chalmers.gamma.internal.group.service.GroupDTO;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.util.domain.Email;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.internal.group.service.GroupShallowDTO;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipFinder;
import it.chalmers.gamma.internal.user.service.UserRestrictedDTO;

import javax.validation.Valid;

import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/groups")
public final class GroupAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdminController.class);
    private final GroupService groupService;
    private final GroupFinder groupFinder;
    private final MembershipFinder membershipFinder;

    public GroupAdminController(GroupService groupService,
                                GroupFinder groupFinder,
                                MembershipFinder membershipFinder) {
        this.groupService = groupService;
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
    }


    @GetMapping()
    public List<GroupWithMembers> getGroups() {
        return this.groupFinder.getAll()
                .stream()
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group, toUserPosts(this.membershipFinder.getMembershipsByGroup(group.id()))
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/active")
    public List<GroupWithMembers> getActiveGroups() {
        return this.groupFinder.getAll()
                .stream()
                .filter(GroupDTO::isActive)
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group, toUserPosts(this.membershipFinder.getMembershipsByGroup(group.id()))
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private record CreateOrEditGroupRequest(String name,
                                            String prettyName,
                                            Calendar becomesActive,
                                            Calendar becomesInactive,
                                            SuperGroupId superGroup,
                                            Email email) { }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateOrEditGroupRequest request) {
        try {
            this.groupService.create(new GroupShallowDTO(
                    null,
                    request.becomesActive,
                    request.becomesInactive,
                    request.email,
                    request.name,
                    request.prettyName,
                    request.superGroup
            ));
        } catch (EntityAlreadyExistsException e) {
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
                    request.becomesActive,
                    request.becomesInactive,
                    request.email,
                    request.name,
                    request.prettyName,
                    request.superGroup
            );
            this.groupService.update(group);
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }

        return new GroupUpdatedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") GroupId id) {
        try {
            this.groupService.delete(id);
        } catch (EntityNotFoundException e) {
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

    private List<UserPost> toUserPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(new UserRestrictedDTO(membership.user()), membership.post()))
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
