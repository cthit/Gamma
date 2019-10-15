package it.chalmers.gamma.controller;

import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.DESCRIPTION;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.EMAIL;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.FUNC;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.GROUP_ID;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.NAME;
import static it.chalmers.gamma.db.serializers.FKITGroupSerializer.Properties.TYPE;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.ACCEPTANCE_YEAR;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.CID;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.FIRST_NAME;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.ID;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.LAST_NAME;
import static it.chalmers.gamma.db.serializers.ITUserSerializer.Properties.NICK;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.views.WebsiteView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressFBWarnings(justification = "I don't know", value = "UC_USELESS_OBJECT")
@SuppressWarnings("PMD.ExcessiveImports")
@RestController
@RequestMapping("/groups")
public final class FKITGroupController {

    //TODO add groupmembers to serialize method call once that has been solved.

    private final FKITGroupService fkitGroupService;
    private final GroupWebsiteService groupWebsiteService;
    private final MembershipService membershipService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;

    public FKITGroupController(
            FKITGroupService fkitGroupService,
            GroupWebsiteService groupWebsiteService,
            MembershipService membershipService, FKITGroupToSuperGroupService fkitGroupToSuperGroupService) {
        this.fkitGroupService = fkitGroupService;
        this.groupWebsiteService = groupWebsiteService;
        this.membershipService = membershipService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public JSONObject getGroup(@PathVariable("id") String id) {
        FKITGroup group;
        // finds the group
        try {
            group = this.fkitGroupService.getGroup(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            group = this.fkitGroupService.getGroup(id);
            if (group == null) {
                throw new GroupDoesNotExistResponse();
            }
        }
        /* Retrieves all websites associated with a
           group ordered after website-type I.E. facebook pages */
        List<WebsiteView> websiteViews =
                this.groupWebsiteService.getWebsitesOrdered(
                        this.groupWebsiteService.getWebsites(group)
                );

        List<ITUser> members = this.membershipService.getUsersInGroup(group);
        List<ITUserSerializer.Properties> props =
                new ArrayList<>(Arrays.asList(
                    CID,
                    FIRST_NAME,
                    LAST_NAME,
                    NICK,
                    ACCEPTANCE_YEAR,
                    ID
                ));
        List<JSONObject> minifiedMembers = new ArrayList<>();
        ITUserSerializer itUserSerializer = new ITUserSerializer(props);
        for (ITUser user : members) {
            JSONObject userObject = itUserSerializer.serialize(user, null, null);
            Membership userMembership = this.membershipService.getMembershipByUserAndGroup(user, group);
            userObject.put("post", userMembership.getId().getPost());
            userObject.put("unofficialPostName", userMembership.getUnofficialPostName());
            minifiedMembers.add(userObject);
        }
        List<FKITSuperGroup> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
        // This should change the database setup probably.
        FKITGroupSerializer serializer = new FKITGroupSerializer(
                // which fields should be sent to frontend
                FKITGroupSerializer.Properties.getAllProperties()
        );
        // serializes all selected data from the group
        return serializer.serialize(group, minifiedMembers, websiteViews, superGroups);
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<JSONObject> getGroupsMinified() {
        List<FKITGroup> groups = this.fkitGroupService.getGroups();
        List<JSONObject> minifiedGroups = new ArrayList<>();
        FKITGroupSerializer serializer = new FKITGroupSerializer(
                Arrays.asList(NAME, FUNC, EMAIL, DESCRIPTION, GROUP_ID, TYPE)
        );
        groups.forEach(fkitGroup -> minifiedGroups.add(
                serializer.serialize(
                        fkitGroup,
                        null,
                        null,
                        null
                )
        ));
        return minifiedGroups;
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public JSONObject getGroupMinified(@PathVariable("id") String id) {
        FKITGroup group = this.fkitGroupService.getGroup(UUID.fromString(id));
        if (group == null) {
            return null;
        }
        FKITGroupSerializer serializer = new FKITGroupSerializer(
                Arrays.asList(NAME, FUNC, GROUP_ID, TYPE)
        );
        return serializer.serialize(group, null, null, null);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<JSONObject> getGroups() {
        List<FKITGroup> groups = this.fkitGroupService.getGroups();
        List<JSONObject> serializedGroups = new ArrayList<>();
        FKITGroupSerializer serializer = new FKITGroupSerializer(FKITGroupSerializer.Properties.getAllProperties());
        ITUserSerializer userSerializer = new ITUserSerializer(
                Arrays.asList(CID, NICK, FIRST_NAME, LAST_NAME, ID));
        for (FKITGroup group : groups) {
            List<ITUser> members = this.membershipService.getUsersInGroup(group);
            List<JSONObject> jsonMembers = new ArrayList<>();
            List<WebsiteView> websites =
                    this.groupWebsiteService.getWebsitesOrdered(this.groupWebsiteService.getWebsites(group));
            for (ITUser member : members) {
                jsonMembers.add(userSerializer.serialize(member, null, null));
            }
            List<FKITSuperGroup> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
            serializedGroups.add(serializer.serialize(
                    group,
                    jsonMembers,
                    websites,
                    superGroups
            ));
        }
        return serializedGroups;
    }
    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public List<JSONObject> getActiveGroups() {
        List<FKITGroup> groups = this.fkitGroupService.getGroups().stream()
                .filter(FKITGroup::isActive).collect(Collectors.toList());
        FKITGroupSerializer groupSerializer = new FKITGroupSerializer(FKITGroupSerializer.Properties
                .getAllProperties());
        ITUserSerializer userSerializer = new ITUserSerializer(ITUserSerializer.Properties.getAllProperties());
        List<JSONObject> serializedUsers = new ArrayList<>();
        List<JSONObject> serializedGroups = new ArrayList<>();
        for (FKITGroup group : groups) {
            List<ITUser> members = this.membershipService.getUsersInGroup(group);
            for (ITUser member : members) {
                serializedUsers.add(userSerializer.serialize(member, null, null));
            }
            List<FKITSuperGroup> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
            serializedGroups.add(groupSerializer.serialize(group, serializedUsers,
                    this.groupWebsiteService.getWebsitesOrdered(this.groupWebsiteService.getWebsites(group)),
                    superGroups));
        }
        System.out.println(serializedGroups.toString());
        return serializedGroups;
    }

}
