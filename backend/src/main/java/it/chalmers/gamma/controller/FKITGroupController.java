package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.Website;
import it.chalmers.gamma.response.GetGroupResponse;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/groups")
public class FKITGroupController {

    private FKITService fkitService;
    private GroupWebsiteService groupWebsiteService;
    public FKITGroupController(FKITService fkitService, GroupWebsiteService groupWebsiteService){
        this.fkitService = fkitService;
        this.groupWebsiteService = groupWebsiteService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FKITGroup.FKITGroupView> getGroup(@PathVariable("id") String id){
        String[] properties = {"avatarURL", "name", "prettyName", "description", "func", "email", "type"};
        FKITGroup group = fkitService.getGroup(UUID.fromString(id));
        if(group == null){
            return new GetGroupResponse(null);
        }
        List<String> props = new ArrayList<>(Arrays.asList(properties));
        FKITGroup.FKITGroupView groupView = group.getView(props);
        List<Website.WebsiteView> websiteViews = groupWebsiteService.getWebsitesOrdered(groupWebsiteService.getWebsites(group));
        groupView.setWebsites(websiteViews);
        return new GetGroupResponse(groupView);
    }
}
