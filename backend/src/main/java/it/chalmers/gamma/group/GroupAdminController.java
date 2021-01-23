package it.chalmers.gamma.group;

import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.group.request.CreateOrEditGroupRequest;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.group.response.GroupCreatedResponse;
import it.chalmers.gamma.group.response.GroupDeletedResponse;
import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.response.GroupEditedResponse;
import it.chalmers.gamma.authoritylevel.AuthorityLevelService;
import it.chalmers.gamma.supergroup.SuperGroupFinder;
import it.chalmers.gamma.supergroup.SuperGroupService;

import it.chalmers.gamma.util.ImageUtils;
import it.chalmers.gamma.util.InputValidationUtils;

import java.util.UUID;

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

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/groups")
public final class GroupAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdminController.class);
    private final GroupService groupService;
    private final SuperGroupService superGroupService;
    private final MembershipService membershipService;
    private final AuthorityLevelService authorityLevelService;

    public GroupAdminController(
            GroupService groupService,
            SuperGroupService superGroupService,
            MembershipService membershipService,
            AuthorityLevelService authorityLevelService, SuperGroupFinder superGroupFinder) {
        this.groupService = groupService;
        this.superGroupService = superGroupService;
        this.membershipService = membershipService;
        this.authorityLevelService = authorityLevelService;
        this.superGroupFinder = superGroupFinder;
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
    public GroupEditedResponse editGroup(
            @RequestBody CreateOrEditGroupRequest request,
            @PathVariable("id") String id) {
        this.groupService.editGroup(requestToDTO(request));
        return new GroupEditedResponse();
    }

    @DeleteMapping("/{id}")
    public GroupDeletedResponse deleteGroup(@PathVariable("id") String id) {
        if (!this.groupService.groupExists(id)) {  // TODO Move to service?
            throw new GroupDoesNotExistResponse();
        }
        this.membershipService.removeAllUsersFromGroup(this.groupService.getGroup(id));
        this.groupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @PutMapping("/{id}/avatar")
    public GroupEditedResponse editAvatar(@PathVariable("id") String id, @RequestParam MultipartFile file) {
        GroupDTO group = this.groupService.getGroup(id);
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        try {
            String url = ImageUtils.saveImage(file, file.getName());
            this.groupService.editGroupAvatar(group, url);
        } catch (FileNotFoundResponse e) {
            throw new FileNotSavedException();
        }
        return new GroupEditedResponse();
    }

    private GroupDTO requestToDTO(CreateOrEditGroupRequest request) {
        return new GroupDTO.GroupDTOBuilder();
    }

    private GroupDTO requestToDTO(UUID id, CreateOrEditGroupRequest request) {

    }

    private GroupDTO.GroupDTOBuilder baseGroupDTOBuilder(CreateOrEditGroupRequest request) {
        return new GroupDTO.GroupDTOBuilder()
                .avatarUrl(request.getAvatarURL())
                .becomesActive(request.getBecomesActive())
                .becomesInactive(request.getBecomesInactive())
                .description(request.getDescription())
                .email(request.getEmail())
                .function(request.getFunction())
                .prettyName(request.getPrettyName())
                .name(request.getName())
                .superGroup(superGroupFinder.getGroup(request.getSuperGroupId())); //orimligt?
    }

}
