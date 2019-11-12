package it.chalmers.gamma.controller;

import com.google.api.client.json.Json;
import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.entity.Post;
import it.chalmers.gamma.db.serializers.GoldappsGroupSerializer;
import it.chalmers.gamma.db.serializers.GoldappsUserSerializer;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.response.GoldappsReponse;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.views.FKITGroupView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.chalmers.gamma.service.PostService;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GoldappsController {

    private final FKITSuperGroupService fkitSuperGroupService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;
    private final MembershipService membershipService;
    private final PostService postService;

    public GoldappsController(FKITSuperGroupService fkitSuperGroupService,
                              FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
                              MembershipService membershipService, PostService postService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.membershipService = membershipService;
        this.postService = postService;
    }

    @RequestMapping(value = "/goldapps", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> getGoldappsData() {
        List<JSONObject> groupsJSON = new ArrayList<>();
        List<JSONObject> usersJSON = new ArrayList<>();

        // Fetch all FKIT groups
        List<FKITGroup> groups = this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .map(this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream)
                .map(c -> c.getId().getGroup())
                .collect(Collectors.toList());

        GoldappsUserSerializer goldappsUserSerializer = new GoldappsUserSerializer();
        GoldappsGroupSerializer goldappsGroupSerializer = new GoldappsGroupSerializer();

        // Go through all groups and serialize them and their users.
        groups.stream().filter(FKITGroup::isActive).forEach(g -> {
                    usersJSON.addAll(
                            this.membershipService.getUsersInGroup(g).stream()
                                    .map(user -> goldappsUserSerializer.serialize(user, null, null))
                                    .collect(Collectors.toList()));
                });
        groups = this.fkitSuperGroupService.getAllGroups()          // Really ugly solution, should be refactored
                .stream()
                .filter(g -> !g.getType().equals(GroupType.ADMIN))
                .map(this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream)
                .map(c -> c.getId().getGroup())
                .distinct()
                .collect(Collectors.toList());

        groups.forEach(g -> groupsJSON.add(
                goldappsGroupSerializer.serialize(g.getEmail(),
                this.membershipService.getUsersInGroup(g).stream()
                .map(user -> this.getCorrectEmail(user, g)).collect(Collectors.toList()))));
        // Construct and send the payload
        List<JSONObject> usersJSONFiltered = usersJSON.stream().distinct().collect(Collectors.toList());
        groupsJSON.addAll(this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.ALUMNI))
                .map(group -> goldappsGroupSerializer.serialize(group.getEmail(),
                        this.fkitGroupToSuperGroupService.getRelationships(group).stream()
                                .map(subgroup -> subgroup.getId().getGroup().getEmail())
                                .collect(Collectors.toList()))).collect(Collectors.toList()));
        List<FKITGroupView> activeGroups = fkitGroupToSuperGroupService.getAllActiveGroups();
        groupsJSON.addAll(activeGroups.stream().map(view -> goldappsGroupSerializer.serialize(view.getSuperGroup().getEmail(),
                view.getGroups().stream().map(FKITGroup::getEmail)
                        .collect(Collectors.toList()))).collect(Collectors.toList()));
        JSONObject payload = new JSONObject();
        List<JSONObject> groupsJSONWithCustom = addCustomGroups(groupsJSON, goldappsGroupSerializer);
        groupsJSONWithCustom = addAliases(groupsJSON, goldappsGroupSerializer);
        payload.put("groups", groupsJSONWithCustom);
        payload.put("users", usersJSONFiltered);

        return new GoldappsReponse(payload);
    }

    private List<JSONObject> addCustomGroups(List<JSONObject> groupsJSON,
                                       GoldappsGroupSerializer goldappsGroupSerializer) {

        groupsJSON.add(goldappsGroupSerializer.serialize("fkit@chalmers.it",
                this.fkitSuperGroupService.getAllGroups().stream().filter(group ->
                        group.getType().equals(GroupType.COMMITTEE) || group.getType().equals(GroupType.SOCIETY))
                        .map(FKITSuperGroup::getEmail).collect(Collectors.toList())));
        this.postService.getAllPosts().stream().filter(post -> !post.getSVPostName().equals("medlem"))
                .forEach(post -> this.membershipService.getMembershipsByPost(post)
                        .forEach(membership -> {
                            groupsJSON.add(goldappsGroupSerializer.serialize(
                                    post.getEmailPrefix() + "." + membership
                                            // This works out a technicality, should be rewritten
                                            .getFkitSuperGroups().get(0).getEmail(),
                                    List.of(this.getCorrectEmail(membership.getId().getITUser(),
                                            membership.getId().getFKITGroup()))));
                        }));
        groupsJSON.add(goldappsGroupSerializer.serialize("kit@chalmers.it",
                this.fkitSuperGroupService.getAllGroups().stream()
                        .filter(g -> g.getType().equals(GroupType.COMMITTEE))
                        .map(FKITSuperGroup::getEmail).collect(Collectors.toList())));
        return groupsJSON;
    }

    private List<JSONObject> addAliases(List<JSONObject> groupsJson, GoldappsGroupSerializer goldappsGroupSerializer) {
        Post kassor = this.postService.getPost("kassör");

        List<FKITSuperGroup> groups = this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .collect(Collectors.toList());

        this.addAliasToJson(goldappsGroupSerializer, groupsJson, groups,
                kassor, "kassorer.kommitteer@chalmers.it");

        Post ordf = this.postService.getPost("ordförande");
        this.addAliasToJson(goldappsGroupSerializer, groupsJson, groups,
                ordf, "ordforanden.kommitteer@chalmers.it");

        List<FKITSuperGroup> committeeGroups = this.fkitSuperGroupService
                .getAllGroups().stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .collect(Collectors.toList());

        this.addAliasToJson(goldappsGroupSerializer, groupsJson, committeeGroups, ordf, "kassorer@chalmers.it");

        List<Membership> chairmen = this.membershipService.getMembershipsByPost(ordf);
        this.addAliasToJson(goldappsGroupSerializer, groupsJson, committeeGroups, ordf, "ordforanden@chalmers.it");

        return groupsJson;
    }
    private List<JSONObject> addAliasToJson(GoldappsGroupSerializer goldappsGroupSerializer,
                                            List<JSONObject> groupsJson,
                                            List<FKITSuperGroup> groups,
                                            Post post,
                                            String mail
                                            ) {
        groupsJson.add(goldappsGroupSerializer.serialize(mail,
                groups.stream().map(group -> post.getEmailPrefix() + "."
                        + group.getEmail()).collect(Collectors.toList())));
        return groupsJson;
    }

    private String getCorrectEmail(ITUser user, FKITGroup group) {
        return this.membershipService.groupIsActiveCommittee(group) ? user.getCid() + "@chalmers.it" : user.getEmail();
    }
}
