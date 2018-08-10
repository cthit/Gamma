package it.chalmers.gamma.controller.administrationSubControllers;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.GroupWebsite;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.requests.CreateGroupRequest;
import it.chalmers.gamma.response.*;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.WebsiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    @RequestMapping(value = "/{group}/minified", method = RequestMethod.GET)
    public ResponseEntity<FKITGroup.FKITGroupView> getGroupMinified(@PathVariable("group") String groupId){
        String[] properties = {"name", "enFunc", "svFunc", "id", "type"};
        FKITGroup group = fkitService.getGroup(groupId);
        if(group == null){
            return new GetGroupResponse(null);
        }
        List<String> props = new ArrayList<>(Arrays.asList(properties));
        FKITGroup.FKITGroupView groupView = group.getView(props);
        return new GetGroupResponse(groupView);
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
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

    @RequestMapping(value = "/{groupId}", method = RequestMethod.PUT)
    public ResponseEntity<String> editGroup(@RequestBody CreateGroupRequest request, @PathVariable("groupId") String groupId) {
        fkitService.editGroup(UUID.fromString(groupId), request.getPrettyName(), request.getDescription(), request.getEmail(),
                request.getType(), request.getFunc(), request.getAvatarURL());
        FKITGroup group = fkitService.getGroup(UUID.fromString(groupId));
        List<CreateGroupRequest.WebsiteInfo> websiteInfos = request.getWebsites();
        List<WebsiteURL> websiteURLs = new ArrayList<>();
        List<GroupWebsite> groupWebsite = groupWebsiteService.getWebsites(group);
        for(CreateGroupRequest.WebsiteInfo websiteInfo : websiteInfos){
            boolean websiteExists = false;
            Website website = websiteService.getWebsite(websiteInfo.getWebsite());
            WebsiteURL websiteURL;
            for(GroupWebsite duplicateCheck : groupWebsite){
                if(duplicateCheck.getWebsite().getUrl().equals(websiteInfo.getUrl())) {
                    websiteExists = true;
                    break;
                }
            }
            if(!websiteExists) {
                websiteURL = new WebsiteURL();
                websiteURL.setWebsite(website);
                websiteURL.setUrl(websiteInfo.getUrl());
                websiteURLs.add(websiteURL);
            }
        }
        groupWebsiteService.addGroupWebsites(group, websiteURLs);
        return new GroupEditedResponse();
    }

    @RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(@PathVariable("groupId") String groupId) {
        if (!fkitService.groupExists(UUID.fromString(groupId))) {
            return new GroupDoesNotExistResponse();
        }
        groupWebsiteService.deleteWebsitesConnectedToGroup(fkitService.getGroup(UUID.fromString(groupId)));
        fkitService.removeGroup(UUID.fromString(groupId));
        return new GroupDeletedResponse();
    }

}
