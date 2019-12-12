package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.response.FileNotSavedException;
import it.chalmers.gamma.response.InputValidationFailedResponse;
import it.chalmers.gamma.response.group.GroupAlreadyExistsResponse;
import it.chalmers.gamma.response.group.GroupCreatedResponse;
import it.chalmers.gamma.response.group.GroupDeletedResponse;
import it.chalmers.gamma.response.group.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.group.GroupEditedResponse;
import it.chalmers.gamma.service.AuthorityLevelService;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.GroupWebsiteService;

import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.WebsiteService;

import it.chalmers.gamma.util.ImageITUtils;
import it.chalmers.gamma.util.InputValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.stream.Collectors;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.AvoidDuplicateLiterals"})
@RestController
@RequestMapping("/admin/groups")
public final class GroupAdminController {

    private final FKITGroupService fkitGroupService;
    private final WebsiteService websiteService;
    private final GroupWebsiteService groupWebsiteService;
    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupAdminController.class);
    private final MembershipService membershipService;
    private final AuthorityLevelService authorityLevelService;

    public GroupAdminController(
            FKITGroupService fkitGroupService,
            WebsiteService websiteService,
            GroupWebsiteService groupWebsiteService,
            FKITSuperGroupService fkitSuperGroupService,
            FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
            MembershipService membershipService,
            AuthorityLevelService authorityLevelService) {
        this.fkitGroupService = fkitGroupService;
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.membershipService = membershipService;
        this.authorityLevelService = authorityLevelService;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    @RequestMapping(method = RequestMethod.POST)
    public GroupCreatedResponse addNewGroup(@Valid @RequestBody CreateGroupRequest createGroupRequest,
                                              BindingResult result) {
        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }
        if (this.fkitGroupService.groupExists(createGroupRequest.getName())) {  // TODO Move check to service?
            throw new GroupAlreadyExistsResponse();
        }

        FKITGroupDTO group = this.fkitGroupService.createGroup(requestToDTO(createGroupRequest));

        List<CreateGroupRequest.WebsiteInfo> websites = createGroupRequest.getWebsites();   // TODO move to service?
        if (websites != null && !websites.isEmpty()) {
            List<WebsiteUrlDTO> websiteURLs = new ArrayList<>();
            for (CreateGroupRequest.WebsiteInfo websiteInfo : websites) {
                WebsiteDTO website = this.websiteService.getWebsite(websiteInfo.getWebsite());
                WebsiteUrlDTO websiteURL = new WebsiteUrlDTO(websiteInfo.getUrl(), website);
                websiteURLs.add(websiteURL);
            }

            try {
                this.groupWebsiteService.addGroupWebsites(group, websiteURLs);
            } catch (DataIntegrityViolationException e) {
                LOGGER.warn(e.getMessage());
                LOGGER.warn("Warning was non-fatal, continuing without adding websites");
            }
        }

        if (createGroupRequest.getSuperGroup() != null) {   // TODO move to service?
            FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(createGroupRequest.getSuperGroup());
            if (superGroup == null) {
                throw new GroupDoesNotExistResponse();
            }

            this.fkitGroupToSuperGroupService.addRelationship(group, superGroup);
        }
        this.authorityLevelService.addAuthorityLevel(group.getName());
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public GroupEditedResponse editGroup(
            @RequestBody CreateGroupRequest request,
            @PathVariable("id") String id) {
        if (!this.fkitGroupService.groupExists(id)) {      // TODO move to service?
            throw new GroupDoesNotExistResponse();
        }
        this.fkitGroupService.editGroup(id, requestToDTO(request));
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(id);
        List<WebsiteUrlDTO> websiteUrlDTOS = request.getWebsites()
                .stream().map(w -> new WebsiteUrlDTO(
                        w.getUrl(),
                        this.websiteService.getWebsite(w.getWebsite()))).collect(Collectors.toList());
        this.groupWebsiteService.addGroupWebsites(group, websiteUrlDTOS);
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public GroupDeletedResponse deleteGroup(@PathVariable("id") String id) {
        if (!this.fkitGroupService.groupExists(id)) {  // TODO Move to service?
            throw new GroupDoesNotExistResponse();
        }
        this.groupWebsiteService.deleteWebsitesConnectedToGroup(
                this.fkitGroupService.getDTOGroup(id)
        );
        this.membershipService.removeAllUsersFromGroup(this.fkitGroupService.getDTOGroup(id));
        this.fkitGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public GroupEditedResponse editAvatar(@PathVariable("id") String id, @RequestParam MultipartFile file) {
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(id);
        if (group == null) {
            throw new GroupDoesNotExistResponse();
        }
        try {
            String url = ImageITUtils.saveImage(file);
            this.fkitGroupService.editGroupAvatar(group, url);
        } catch (IOException e) {
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
                request.getAvatarURL()
        );
    }

}
