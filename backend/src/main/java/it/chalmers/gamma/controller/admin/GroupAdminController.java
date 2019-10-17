package it.chalmers.delta.controller.admin;

import it.chalmers.delta.db.entity.FKITGroup;
import it.chalmers.delta.db.entity.FKITSuperGroup;
import it.chalmers.delta.db.entity.Website;
import it.chalmers.delta.db.entity.WebsiteInterface;
import it.chalmers.delta.db.entity.WebsiteURL;

import it.chalmers.delta.requests.CreateGroupRequest;
import it.chalmers.delta.response.FileNotSavedException;
import it.chalmers.delta.response.GroupAlreadyExistsResponse;
import it.chalmers.delta.response.GroupCreatedResponse;
import it.chalmers.delta.response.GroupDeletedResponse;
import it.chalmers.delta.response.GroupDoesNotExistResponse;
import it.chalmers.delta.response.GroupEditedResponse;
import it.chalmers.delta.response.InputValidationFailedResponse;
import it.chalmers.delta.service.AuthorityLevelService;
import it.chalmers.delta.service.FKITGroupService;
import it.chalmers.delta.service.FKITGroupToSuperGroupService;
import it.chalmers.delta.service.FKITSuperGroupService;
import it.chalmers.delta.service.GroupWebsiteService;

import it.chalmers.delta.service.MembershipService;
import it.chalmers.delta.service.WebsiteService;

import it.chalmers.delta.util.ImageITUtils;
import it.chalmers.delta.util.InputValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> addNewGroup(@Valid @RequestBody CreateGroupRequest createGroupRequest,
                                              BindingResult result) {
        if (this.fkitGroupService.groupExists(createGroupRequest.getName())) {
            throw new GroupAlreadyExistsResponse();
        }

        if (result.hasErrors()) {
            throw new InputValidationFailedResponse(InputValidationUtils.getErrorMessages(result.getAllErrors()));
        }

        FKITGroup group = this.fkitGroupService.createGroup(createGroupRequest);

        List<CreateGroupRequest.WebsiteInfo> websites = createGroupRequest.getWebsites();
        if (websites != null && !websites.isEmpty()) {
            List<WebsiteURL> websiteURLs = new ArrayList<>();
            for (CreateGroupRequest.WebsiteInfo websiteInfo : websites) {
                Website website = this.websiteService.getWebsite(websiteInfo.getWebsite());
                WebsiteURL websiteURL = new WebsiteURL();
                websiteURL.setWebsite(website);
                websiteURL.setUrl(websiteInfo.getUrl());
                websiteURLs.add(websiteURL);
            }

            try {
                this.groupWebsiteService.addGroupWebsites(group, websiteURLs);
            } catch (DataIntegrityViolationException e) {
                LOGGER.warn(e.getMessage());
                LOGGER.warn("Warning was non-fatal, continuing without adding websites");
            }
        }

        if (createGroupRequest.getSuperGroup() != null) {
            FKITSuperGroup superGroup = this.fkitSuperGroupService.getGroup(
                    UUID.fromString(createGroupRequest.getSuperGroup()));
            if (superGroup == null) {
                throw new GroupDoesNotExistResponse();
            }

            this.fkitGroupToSuperGroupService.addRelationship(group, superGroup);
        }

        // Adds each group as an authoritylevel which
        this.authorityLevelService.addAuthorityLevel(group.getName());
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGroup(
            @RequestBody CreateGroupRequest request,
            @PathVariable("id") String id) {
        if (!this.fkitGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        this.fkitGroupService.editGroup(UUID.fromString(id), request);
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(id));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteInterface> entityWebsites = new ArrayList<>(
                this.groupWebsiteService.getWebsites(group)
        );
        List<WebsiteURL> websiteURLs = this.groupWebsiteService.addWebsiteToEntity(
                websiteInfos, entityWebsites
        );
        this.groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("id") String id) {
        if (!this.fkitGroupService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        this.groupWebsiteService.deleteWebsitesConnectedToGroup(
                this.fkitGroupService.getGroup(UUID.fromString(id))
        );
        this.membershipService.removeAllUsersFromGroup(this.fkitGroupService.getGroup(UUID.fromString(id)));
        this.fkitGroupService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

    @RequestMapping(value = "/{id}/avatar", method = RequestMethod.PUT)
    public ResponseEntity<String> editAvatar(@PathVariable("id") String id, @RequestParam MultipartFile file) {
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(id));
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

}
