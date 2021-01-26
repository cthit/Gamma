package it.chalmers.gamma.group.controller;

import it.chalmers.gamma.domain.IDsNotMatchingException;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.group.dto.GroupShallowDTO;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.group.controller.request.CreateOrEditGroupRequest;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.group.controller.response.GroupCreatedResponse;
import it.chalmers.gamma.group.controller.response.GroupDeletedResponse;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.controller.response.GroupEditedResponse;

import it.chalmers.gamma.util.ImageUtils;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

import javax.validation.Valid;

import it.chalmers.gamma.util.InternalServerErrorResponse;
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
    private final MembershipService membershipService;

    public GroupAdminController(GroupService groupService, MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateOrEditGroupRequest createOrEditGroupRequest,
                                            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        this.groupService.createGroup(requestToDTO(createOrEditGroupRequest));

        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupEditedResponse editGroup(@Valid @RequestBody CreateOrEditGroupRequest request,
            @PathVariable("id") UUID id,
            BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        try {
            this.groupService.editGroup(requestToDTO(request, id));
        } catch (GroupNotFoundException e) {
            throw new GroupDoesNotExistResponse();
        } catch (IDsNotMatchingException e) {
            throw new InternalServerErrorResponse();
        }

        return new GroupEditedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") UUID id) {
        this.membershipService.removeAllUsersFromGroup(id);
        this.groupService.removeGroup(id);

        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}/avatar")
    public GroupEditedResponse editAvatar(@PathVariable("id") UUID id, @RequestParam MultipartFile file) {
        try {
            String url = ImageUtils.saveImage(file, file.getName());
            this.groupService.editGroupAvatar(id, url);
        } catch (FileNotFoundResponse e) {
            throw new FileNotSavedException();
        } catch (GroupNotFoundException e) {
            throw new GroupDoesNotExistResponse();
        }
        return new GroupEditedResponse();
    }

    private GroupShallowDTO requestToDTO(CreateOrEditGroupRequest request) {
        return baseGroupDTOBuilder(request).id(UUID.randomUUID()).build();
    }

    private GroupShallowDTO requestToDTO(CreateOrEditGroupRequest request, UUID id) {
        return baseGroupDTOBuilder(request).id(id).build();
    }

    private GroupShallowDTO.GroupShallowDTOBuilder baseGroupDTOBuilder(CreateOrEditGroupRequest request) {
        return new GroupShallowDTO.GroupShallowDTOBuilder()
                .avatarUrl(request.getAvatarURL())
                .becomesActive(request.getBecomesActive())
                .becomesInactive(request.getBecomesInactive())
                .description(request.getDescription())
                .email(request.getEmail())
                .function(request.getFunction())
                .prettyName(request.getPrettyName())
                .name(request.getName())
                .superGroupId(request.getSuperGroupId()
        );
    }

}
