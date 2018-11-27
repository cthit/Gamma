package it.chalmers.gamma.controller.admin;

import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.FUNC;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.ID;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.NAME;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.TYPE;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteInterface;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.response.GroupAlreadyExistsResponse;
import it.chalmers.gamma.response.GroupCreatedResponse;
import it.chalmers.gamma.response.GroupDeletedResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.GroupEditedResponse;
import it.chalmers.gamma.response.GroupsResponse;
import it.chalmers.gamma.response.MissingRequiredFieldResponse;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.WebsiteService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/groups")
public class GroupAdminController {
    private FKITService fkitService;
    private WebsiteService websiteService;
    private GroupWebsiteService groupWebsiteService;

    public GroupAdminController(
            FKITService fkitService,
            WebsiteService websiteService,
            GroupWebsiteService groupWebsiteService) {
        this.fkitService = fkitService;
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getGroups() {
        return new GroupsResponse(this.fkitService.getGroups());
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public JSONObject getGroupMinified(@PathVariable("id") String id) {
        List<FKITGroupSerializer.Properties> properties = Arrays.asList(NAME, FUNC, ID, TYPE);
        FKITGroup group = this.fkitService.getGroup(UUID.fromString(id));
        if (group == null) {
            return null;
        }
        FKITGroupSerializer serializer = new FKITGroupSerializer(properties);
        return serializer.serialize(group, null, null);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addNewGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        if (this.fkitService.groupExists(createGroupRequest.getName())) {
            throw new GroupAlreadyExistsResponse();
        }
        if (createGroupRequest.getName() == null) {
            throw new MissingRequiredFieldResponse("name");
        }
        if (createGroupRequest.getEmail() == null) {
            throw new MissingRequiredFieldResponse("email");
        }
        if (createGroupRequest.getFunc() == null) {
            throw new MissingRequiredFieldResponse("function");
        }
        if (createGroupRequest.getType() == null) {
            throw new MissingRequiredFieldResponse("type");
        }
        FKITGroup group = this.fkitService.createGroup(
                createGroupRequest.getName(),
                createGroupRequest.getPrettyName(),
                createGroupRequest.getDescription(),
                createGroupRequest.getEmail(),
                createGroupRequest.getType(),
                createGroupRequest.getFunc(),
                createGroupRequest.getAvatarURL()
        );
        List<CreateGroupRequest.WebsiteInfo> websites = createGroupRequest.getWebsites();
        if (websites == null || websites.isEmpty()) {
            return new GroupCreatedResponse();
        }
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for (CreateGroupRequest.WebsiteInfo websiteInfo : websites) {
            Website website = this.websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL = new WebsiteURL();
            websiteURL.setWebsite(website);
            websiteURL.setUrl(websiteInfo.getUrl());
            websiteURLs.add(websiteURL);
        }
        this.groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGroup(
            @RequestBody CreateGroupRequest request,
            @PathVariable("id") String id) {
        this.fkitService.editGroup(
                UUID.fromString(id),
                request.getPrettyName(),
                request.getDescription(),
                request.getEmail(),
                request.getType(),
                request.getFunc(),
                request.getAvatarURL());
        FKITGroup group = this.fkitService.getGroup(UUID.fromString(id));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteInterface> entityWebsites = new ArrayList<>(this.groupWebsiteService.getWebsites(group));
        List<WebsiteURL> websiteURLs = this.groupWebsiteService.addWebsiteToEntity(websiteInfos, entityWebsites);
        this.groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("id") String id) {
        if (!this.fkitService.groupExists(UUID.fromString(id))) {
            throw new GroupDoesNotExistResponse();
        }
        this.groupWebsiteService.deleteWebsitesConnectedToGroup(
                this.fkitService.getGroup(UUID.fromString(id))
        );
        this.fkitService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

}
