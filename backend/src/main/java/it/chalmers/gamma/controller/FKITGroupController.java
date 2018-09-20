package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.service.EntityWebsiteService;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.MembershipService;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/groups")
public class FKITGroupController {

    //TODO add groupmembers to serialize method call once that has been solved.

    private FKITService fkitService;
    private GroupWebsiteService groupWebsiteService;
    private MembershipService membershipService;

    public FKITGroupController(FKITService fkitService, GroupWebsiteService groupWebsiteService, MembershipService membershipService){
        this.fkitService = fkitService;
        this.groupWebsiteService = groupWebsiteService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject getGroup(@PathVariable("id") String id){
        List<FKITGroupSerializer.Properties> properties = FKITGroupSerializer.Properties.getAllProperties();    // which fields should be sent to frontend
        FKITGroup group = fkitService.getGroup(UUID.fromString(id));    // finds the group
        if(group == null){      //makes sure that the requested group exists
            return null;
        }
        List<EntityWebsiteService.WebsiteView> websiteViews = groupWebsiteService.
                getWebsitesOrdered(groupWebsiteService.getWebsites(group));     // Retrieves all websites associated with a group ordered after website-type I.E. facebook pages
        List<UUID> membersUUIDs = membershipService.getUsersInGroup(group); // This should change the database setup probably.
        FKITGroupSerializer serializer = new FKITGroupSerializer(properties);
        return serializer.serialize(group, null, websiteViews);    // serializes all selected data from the group

    }
}
