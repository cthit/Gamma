package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.*;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.WebsiteService;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.*;

@RestController
@RequestMapping(value = "/admin/groups")
public class GroupAdminController {
    private FKITService fkitService;
    private WebsiteService websiteService;
    private GroupWebsiteService groupWebsiteService;
    public GroupAdminController(FKITService fkitService, WebsiteService websiteService, GroupWebsiteService groupWebsiteService){
        this.fkitService = fkitService;
        this.websiteService = websiteService;
        this.groupWebsiteService = groupWebsiteService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<FKITGroup>> getGroups(){
        return new GroupsResponse(fkitService.getGroups());
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public JSONObject getGroupMinified(@PathVariable("id") String id){
        List<FKITGroupSerializer.Properties> properties = Arrays.asList(NAME, FUNC, ID, TYPE);
        FKITGroup group = fkitService.getGroup(UUID.fromString(id));
        if(group == null){
            return null;
        }
        FKITGroupSerializer serializer = new FKITGroupSerializer(properties);
        return serializer.serialize(group, null, null);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addNewGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        if (fkitService.groupExists(createGroupRequest.getName())) {
            return new GroupAlreadyExistsResponse();
        }
        if (createGroupRequest.getName() == null) {
            return new MissingRequiredFieldResponse("name");
        }
        if (createGroupRequest.getEmail() == null) {
            return new MissingRequiredFieldResponse("email");
        }
        if (createGroupRequest.getFunc() == null) {
            return new MissingRequiredFieldResponse("function");
        }
        if (createGroupRequest.getType() == null) {
            return new MissingRequiredFieldResponse("type");
        }
        FKITGroup group = fkitService.createGroup(createGroupRequest.getName(), createGroupRequest.getPrettyName(), createGroupRequest.getDescription(),
                createGroupRequest.getEmail(), createGroupRequest.getType(), createGroupRequest.getFunc(), createGroupRequest.getAvatarURL());
        List<CreateGroupRequest.WebsiteInfo> websites = createGroupRequest.getWebsites();
        if(websites == null || websites.isEmpty()){
            return new GroupCreatedResponse();
        }
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websites){
            Website website = websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL = new WebsiteURL();
            websiteURL.setWebsite(website);
            websiteURL.setUrl(websiteInfo.getUrl());
            websiteURLs.add(websiteURL);
        }
        groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupCreatedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGroup(@RequestBody CreateGroupRequest request, @PathVariable("id") String id) {
        fkitService.editGroup(UUID.fromString(id), request.getPrettyName(), request.getDescription(), request.getEmail(),
                request.getType(), request.getFunc(), request.getAvatarURL());
        FKITGroup group = fkitService.getGroup(UUID.fromString(id));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteInterface> entityWebsites = new ArrayList<>(groupWebsiteService.getWebsites(group));
        List<WebsiteURL> websiteURLs = groupWebsiteService.addWebsiteToEntity(websiteInfos, entityWebsites);
        groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("id") String id) {
        if (!fkitService.groupExists(UUID.fromString(id))) {
            return new GroupDoesNotExistResponse();
        }
        groupWebsiteService.deleteWebsitesConnectedToGroup(fkitService.getGroup(UUID.fromString(id)));
        fkitService.removeGroup(UUID.fromString(id));
        return new GroupDeletedResponse();
    }

}
