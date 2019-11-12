package it.chalmers.gamma.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.view.FKITGroupView;
import it.chalmers.gamma.response.view.ITUserView;
import it.chalmers.gamma.response.view.MembershipView;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.views.WebsiteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{id}")
    public FKITGroupView getGroup(@PathVariable("id") String id) {
        final FKITGroup group = this.getGroupByIdOrName(id);

        List<FKITSuperGroup> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
        // This should change the database setup probably.

        if (superGroups.isEmpty()) {
            throw new RuntimeException("Why supergroup is empty?!");
        }

        /* Retrieves all websites associated with a
           group ordered after website-type I.E. facebook pages */
        List<WebsiteView> websiteViews =
                this.groupWebsiteService.getWebsitesOrdered(
                        this.groupWebsiteService.getWebsites(group)
                );

        List<MembershipView> minifiedMembers = this.membershipService.getUsersInGroup(group).stream()
                .map(user -> {
                    Membership userMembership = this.membershipService.getMembershipByUserAndGroup(user, group);
                    return new MembershipView(userMembership.getId().getPost(),
                            userMembership.getUnofficialPostName(),
                            new ITUserView(user.getId(), user.getCid(), user.getNick(), user.getFirstName(),
                                    user.getLastName(), null, null, null, null,
                                    user.isGdpr(), user.isUserAgreement(), user.isAccountLocked(),
                                    user.getAcceptanceYear(), null));

                }).collect(Collectors.toList());

        return new FKITGroupView(
                group.getId(),
                group.getBecomesActive(),
                group.getBecomesInactive(),
                group.getDescription(),
                group.getEmail(),
                group.getFunction(),
                group.isActive(),
                group.getName(),
                group.getPrettyName(),
                minifiedMembers,
                superGroups,
                websiteViews
        );
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<FKITGroupView> getGroupsMinified() {
        List<FKITGroup> groups = this.fkitGroupService.getGroups();
        return groups.stream().map(g -> new FKITGroupView(
                g.getId(),
                null,
                null,
                g.getDescription(),
                g.getEmail(),
                g.getFunction(),
                g.isActive(),
                g.getName(),
                null,
                null,
                null,
                null
        )).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public FKITGroupView getGroupMinified(@PathVariable("id") String id) {
        final FKITGroup group = this.getGroupByIdOrName(id);
        return new FKITGroupView(
                group.getId(),
                null,
                null,
                null,
                group.getEmail(),
                group.getFunction(),
                group.isActive(),
                group.getName(),
                null,
                null,
                null,
                null
        );
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<FKITGroupView> getGroups() {
        List<FKITGroup> groups = this.fkitGroupService.getGroups();
        return new ArrayList<>(groups.stream().map(g -> new FKITGroupView(
                g.getId(),
                g.getBecomesActive(),
                g.getBecomesInactive(),
                g.getDescription(),
                g.getEmail(),
                g.getFunction(),
                g.isActive(),
                g.getName(),
                g.getPrettyName(),
                this.membershipService.getUsersInGroup(g).stream().map(user -> {
                    Membership membership = this.membershipService.getMembershipByUserAndGroup(user, g);
                    return new MembershipView(membership.getId().getPost(),
                            membership.getUnofficialPostName(),
                            new ITUserView(user.getId(), user.getCid(), user.getNick(), user.getFirstName(),
                                    user.getLastName(), null, null, null, null,
                                    user.isGdpr(), user.isUserAgreement(), user.isAccountLocked(),
                                    user.getAcceptanceYear(), null));
                }).collect(Collectors.toList()),
                this.fkitGroupToSuperGroupService.getSuperGroups(g),
                this.groupWebsiteService.getWebsitesOrdered(this.groupWebsiteService.getWebsites(g))
        )).collect(Collectors.toList()));
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
        return serializedGroups;
    }

    private FKITGroup getGroupByIdOrName(String idOrName) throws GroupDoesNotExistResponse {
        return Optional.ofNullable(this.fkitGroupService.getGroup(UUID.fromString(idOrName)))
                .or(() -> Optional.ofNullable(this.fkitGroupService.getGroup(idOrName)))
                .orElseThrow(GroupDoesNotExistResponse::new);
    }

}
