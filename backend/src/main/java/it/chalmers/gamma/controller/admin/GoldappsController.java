package it.chalmers.gamma.controller.admin;

import it.chalmers.gamma.db.serializers.GoldappsGroupSerializer;
import it.chalmers.gamma.db.serializers.GoldappsUserSerializer;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupToSuperGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.post.PostDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.response.GoldappsReponse;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.service.PostService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/admin/goldapps", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @GetMapping()
    public ResponseEntity<JSONObject> getGoldappsData() {
        List<JSONObject> groupsJSON = new ArrayList<>();
        List<JSONObject> usersJSON = new ArrayList<>();

        // Fetch all FKIT groups
        List<FKITGroupDTO> groups = this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .map(this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream)
                .map(FKITGroupToSuperGroupDTO::getGroup)
                .collect(Collectors.toList());

        GoldappsUserSerializer goldappsUserSerializer = new GoldappsUserSerializer();
        GoldappsGroupSerializer goldappsGroupSerializer = new GoldappsGroupSerializer();

        // Go through all groups and serialize them and their users.
        groups.stream().filter(FKITGroupDTO::isActive).forEach(g -> {
            usersJSON.addAll(
                    this.membershipService.getMembershipsInGroup(g).stream()
                            .map(m -> goldappsUserSerializer.serialize(m.getUser()))
                            .collect(Collectors.toList()));
        });
        groups = this.fkitSuperGroupService.getAllGroups()          // Really ugly solution, should be refactored
                .stream()
                .filter(g -> !g.getType().equals(GroupType.ADMIN))
                .map(this.fkitGroupToSuperGroupService::getRelationships)
                .flatMap(Collection::stream)
                .map(FKITGroupToSuperGroupDTO::getGroup)
                .distinct()
                .collect(Collectors.toList());

        groups.forEach(g -> groupsJSON.add(
                goldappsGroupSerializer.serialize(g.getEmail(),
                        this.membershipService.getMembershipsInGroup(g).stream()
                                .map(m -> this.getCorrectEmail(m.getUser(), g)).collect(Collectors.toList()))));
        // Construct and send the payload

        List<FKITGroupDTO> activeGroups = this.fkitGroupToSuperGroupService.getAllActiveGroups();
        groupsJSON.addAll(activeGroups.stream()
                .map(g ->
                    goldappsGroupSerializer.serialize(this.fkitGroupToSuperGroupService
                                    .getSuperGroups(g).get(0).getEmail(),
                    this.fkitGroupToSuperGroupService.getRelationships(
                            this.fkitGroupToSuperGroupService.getSuperGroups(g).get(0))
                            .stream().map(g2 -> g2.getGroup().getEmail()).collect(Collectors.toList())))
                .collect(Collectors.toList()));
        JSONObject payload = new JSONObject();
        List<JSONObject> groupsJSONWithCustom = addCustomGroups(groupsJSON, goldappsGroupSerializer);
        groupsJSONWithCustom = addAliases(groupsJSON, goldappsGroupSerializer);

        List<JSONObject> usersJSONFiltered = usersJSON.stream().distinct().collect(Collectors.toList());
        groupsJSON.addAll(this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.ALUMNI))
                .map(group -> goldappsGroupSerializer.serialize(group.getEmail(),
                        this.fkitGroupToSuperGroupService.getRelationships(group).stream()
                                .map(subgroup -> subgroup.getGroup().getEmail())
                                .collect(Collectors.toList()))).collect(Collectors.toList()));
        payload.put("groups", groupsJSONWithCustom);
        payload.put("users", usersJSONFiltered);

        return new GoldappsReponse(payload);
    }

    private List<JSONObject> addCustomGroups(List<JSONObject> groupsJSON,
                                             GoldappsGroupSerializer goldappsGroupSerializer) {

        groupsJSON.add(goldappsGroupSerializer.serialize("fkit@chalmers.it",
                this.fkitSuperGroupService.getAllGroups().stream().filter(group ->
                        group.getType().equals(GroupType.COMMITTEE) || group.getType().equals(GroupType.SOCIETY))
                        .map(FKITSuperGroupDTO::getEmail).collect(Collectors.toList())));
        this.postService.getAllPosts().stream().filter(post -> !post.getPostName().getSv().equals("medlem"))
                .forEach(post -> this.membershipService.getMembershipsByPost(post)
                        .forEach(membership -> {
                            groupsJSON.add(goldappsGroupSerializer.serialize(
                                    post.getEmailPrefix() + "." + membership
                                            // This works out a technicality, should be rewritten
                                            .getFkitSuperGroups().get(0).getEmail(),
                                    List.of(this.getCorrectEmail(membership.getId().getITUser().toDTO(),
                                            membership.getId().getFKITGroup().toDTO()))));
                        }));
        groupsJSON.add(goldappsGroupSerializer.serialize("kit@chalmers.it",
                this.fkitSuperGroupService.getAllGroups().stream()
                        .filter(g -> g.getType().equals(GroupType.COMMITTEE))
                        .map(FKITSuperGroupDTO::getEmail).collect(Collectors.toList())));
        return groupsJSON;
    }

    private List<JSONObject> addAliases(List<JSONObject> groupsJson, GoldappsGroupSerializer goldappsGroupSerializer) {
        PostDTO kassor = this.postService.getPostDTO("kassör");

        List<FKITSuperGroupDTO> groups = this.fkitSuperGroupService.getAllGroups()
                .stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .collect(Collectors.toList());

        this.addAliasToJson(goldappsGroupSerializer, groupsJson, groups,
                kassor, "kassorer.kommitteer@chalmers.it");

        PostDTO ordf = this.postService.getPostDTO("ordförande");
        this.addAliasToJson(goldappsGroupSerializer, groupsJson, groups,
                ordf, "ordforanden.kommitteer@chalmers.it");

        List<FKITSuperGroupDTO> committeeGroups = this.fkitSuperGroupService
                .getAllGroups().stream().filter(g -> g.getType().equals(GroupType.COMMITTEE))
                .collect(Collectors.toList());

        this.addAliasToJson(goldappsGroupSerializer, groupsJson, committeeGroups, ordf, "kassorer@chalmers.it");

        this.addAliasToJson(goldappsGroupSerializer, groupsJson, committeeGroups, ordf, "ordforanden@chalmers.it");

        return groupsJson;
    }

    private List<JSONObject> addAliasToJson(GoldappsGroupSerializer goldappsGroupSerializer,
                                            List<JSONObject> groupsJson,
                                            List<FKITSuperGroupDTO> groups,
                                            PostDTO post,
                                            String mail
    ) {
        groupsJson.add(goldappsGroupSerializer.serialize(mail,
                groups.stream().map(group -> post.getEmailPrefix() + "."
                        + group.getEmail()).collect(Collectors.toList())));
        return groupsJson;
    }

    private String getCorrectEmail(ITUserDTO user, FKITGroupDTO group) {
        return this.membershipService.groupIsActiveCommittee(group) ? user.getCid() + "@chalmers.it" : user.getEmail();
    }
}
