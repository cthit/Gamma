package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.service.EntityWebsiteService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

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
    public JSONObject getGroup(@PathVariable("id") String id){
        List<FKITGroupSerializer.Properties> properties = FKITGroupSerializer.Properties.getAllProperties();
        FKITGroup group = fkitService.getGroup(UUID.fromString(id));
        if(group == null){
            return null;
        }
        List<EntityWebsiteService.WebsiteView> websiteViews = groupWebsiteService.getWebsitesOrdered(groupWebsiteService.getWebsites(group));
        FKITGroupSerializer serializer = new FKITGroupSerializer(properties);
        return serializer.serialize(group, null, websiteViews);

    }
}
