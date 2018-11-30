package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.service.FKITService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.WebsiteView;

import java.util.List;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public final class FKITGroupController {

    //TODO add groupmembers to serialize method call once that has been solved.

    private final FKITService fkitService;
    private final GroupWebsiteService groupWebsiteService;

    private FKITGroupController(
            FKITService fkitService,
            GroupWebsiteService groupWebsiteService) {
        this.fkitService = fkitService;
        this.groupWebsiteService = groupWebsiteService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject getGroup(@PathVariable("id") String id) {

        // finds the group
        FKITGroup group = this.fkitService.getGroup(UUID.fromString(id));
        if (group == null) {      //makes sure that the requested group exists
            return null;
        }
        /* Retrieves all websites associated with a
           group ordered after website-type I.E. facebook pages */
        List<WebsiteView> websiteViews =
                this.groupWebsiteService.getWebsitesOrdered(
                        this.groupWebsiteService.getWebsites(group)
                );
        // This should change the database setup probably.
        FKITGroupSerializer serializer = new FKITGroupSerializer(
                // which fields should be sent to frontend
                FKITGroupSerializer.Properties.getAllProperties()
        );
        // serializes all selected data from the group
        return serializer.serialize(group, null, websiteViews);
    }
}
