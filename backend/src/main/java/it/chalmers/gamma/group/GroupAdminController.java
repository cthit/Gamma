package it.chalmers.gamma.group;

import it.chalmers.gamma.supergroup.FKITSuperGroupDTO;
import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.group.request.CreateGroupRequest;
import it.chalmers.gamma.response.FileNotFoundResponse;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.group.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.group.response.GroupCreatedResponse;
import it.chalmers.gamma.group.response.GroupDeletedResponse;
import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.response.GroupEditedResponse;
import it.chalmers.gamma.authority.AuthorityLevelService;
import it.chalmers.gamma.supergroup.FKITSuperGroupService;

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

    private final GroupService groupService;
    private final FKITSuperGroupService fkitSuperGroupService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdminController.class);
    private final MembershipService membershipService;
    private final AuthorityLevelService authorityLevelService;

    public GroupAdminController(
            GroupService groupService,
            FKITSuperGroupService fkitSuperGroupService,
            MembershipService membershipService,
            AuthorityLevelService authorityLevelService) {
        this.groupService = groupService;
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.membershipService = membershipService;
        this.authorityLevelService = authorityLevelService;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @PostMapping()
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateGroupRequest createGroupRequest,
                                              BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.groupService.groupExists(createGroupRequest.getName())) {  // TODO Move check to service?
            throw new GroupAlreadyExistsResponse();
        }

        FKITGroupDTO group = this.groupService.createGroup(requestToDTO(createGroupRequest));

        if (createGroupRequest.getSuperGroup() != null) {   // TODO move to service?
            FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(createGroupRequest.getSuperGroup());
            if (superGroup == null) {
                throw new GroupDoesNotExistResponse();
            }
        }
        this.authorityLevelService.addAuthorityLevel(group.getName());
        return new GroupCreatedResponse();
    }

    @PutMapping("/{id}")
    public GroupEditedResponse editGroup(
            @RequestBody CreateGroupRequest request,
            @PathVariable("id") String id) {
        if (!this.groupService.groupExists(id)) {      // TODO move to service?
            throw new GroupDoesNotExistResponse();
        }
        this.groupService.editGroup(id, requestToDTO(request));
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
        FKITGroupDTO group = this.groupService.getGroup(id);
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

    private FKITGroupDTO requestToDTO(CreateGroupRequest request) {
        return new FKITGroupDTO(
                request.getBecomesActive(),
                request.getBecomesInactive(),
                request.getDescription(),
                request.getEmail(),
                request.getFunction(),
                request.getName(),
                request.getPrettyName(),
                request.getAvatarURL(),
                this.fkitSuperGroupService.getGroupDTO(request.getSuperGroup())
        );
    }

}
