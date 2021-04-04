package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityAlreadyExistsException;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.util.domain.GroupWithMembers;
import it.chalmers.gamma.util.domain.UserPost;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.controller.response.*;
import it.chalmers.gamma.domain.group.data.dto.GroupBaseDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.group.data.dto.GroupShallowDTO;
import it.chalmers.gamma.domain.membership.data.dto.MembershipDTO;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.group.controller.request.CreateOrEditGroupRequest;
import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public GetAllGroupResponse getGroups() {
        List<GroupWithMembers> groups = this.groupFinder.getAll()
                .stream()
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group, toUserPosts(this.membershipFinder.getMembershipsByGroup(group.getId()))
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return new GetAllGroupResponse(groups);
    }

    @GetMapping("/active")
    public GetAllGroupResponse getActiveGroups() {
        List<GroupWithMembers> groups = this.groupFinder.getAll()
                .stream()
                .filter(GroupBaseDTO::isActive)
                .map(group -> {
                    try {
                        return new GroupWithMembers(
                                group, toUserPosts(this.membershipFinder.getMembershipsByGroup(group.getId()))
                        );
                    } catch (EntityNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return new GetAllGroupResponse(groups);
    }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateOrEditGroupRequest createOrEditGroupRequest) {
        try {
            this.groupService.create(requestToDTO(createOrEditGroupRequest));
        } catch (EntityAlreadyExistsException e) {
            LOGGER.error("Error when trying to create a group that already exists", e);
            throw new GroupAlreadyExistsResponse();
        }

        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupUpdatedResponse editGroup(@Valid @RequestBody CreateOrEditGroupRequest request,
                                          @PathVariable("id") GroupId id) {
        try {
            GroupShallowDTO group = requestToDTO(request, id);
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

    private GroupShallowDTO requestToDTO(CreateOrEditGroupRequest request) {
        return baseGroupDTOBuilder(request).build();
    }

    private GroupShallowDTO requestToDTO(CreateOrEditGroupRequest request, GroupId id) {
        return baseGroupDTOBuilder(request).id(id).build();
    }

    private GroupShallowDTO.GroupShallowDTOBuilder baseGroupDTOBuilder(CreateOrEditGroupRequest request) {
        return new GroupShallowDTO.GroupShallowDTOBuilder()
                .avatarUrl(request.avatarURL)
                .becomesActive(request.becomesActive)
                .becomesInactive(request.becomesInactive)
                .email(request.email)
                .prettyName(request.prettyName)
                .name(request.name)
                .superGroupId(request.superGroup);
    }

    private List<UserPost> toUserPosts(List<MembershipDTO> memberships) {
        return memberships
                .stream()
                .map(membership -> new UserPost(new UserRestrictedDTO(membership.getUser()), membership.getPost()))
                .collect(Collectors.toList());
    }

}
