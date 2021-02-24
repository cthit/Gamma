package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.domain.EntityAlreadyExistsException;
import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.controller.response.*;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.group.data.GroupShallowDTO;
import it.chalmers.gamma.domain.membership.service.MembershipService;
import it.chalmers.gamma.domain.group.controller.request.CreateOrEditGroupRequest;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;

import it.chalmers.gamma.util.ImageUtils;
import it.chalmers.gamma.util.InputValidationUtils;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/groups")
public final class GroupAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdminController.class);
    private final GroupService groupService;
    private final GroupFinder groupFinder;

    public GroupAdminController(GroupService groupService,
                                GroupFinder groupFinder) {
        this.groupService = groupService;
        this.groupFinder = groupFinder;
    }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateOrEditGroupRequest createOrEditGroupRequest,
                                            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

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
                                          @PathVariable("id") GroupId id,
                                          BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
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

}
