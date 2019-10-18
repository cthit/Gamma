package it.chalmers.gamma.controller;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.serializers.GoldappsGroupSerializer;
import it.chalmers.gamma.db.serializers.GoldappsUserSerializer;
import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.response.GoldappsReponse;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.MembershipService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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

    public GoldappsController(FKITSuperGroupService fkitSuperGroupService,
                              FKITGroupToSuperGroupService fkitGroupToSuperGroupService,
                              MembershipService membershipService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
        this.membershipService = membershipService;
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
        groups.forEach(g -> {
            groupsJSON.add(
                    goldappsGroupSerializer.serialize(g,
                    this.membershipService.getUsersInGroup(g).stream()
                    .map(ITUser::getEmail).collect(Collectors.toList())));
        });

        // Construct and send the payload
        JSONObject payload = new JSONObject();
        payload.put("groups", groupsJSON);
        payload.put("users", usersJSON);

        return new GoldappsReponse(payload);
    }

}
