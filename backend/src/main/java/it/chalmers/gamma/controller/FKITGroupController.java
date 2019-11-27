package it.chalmers.gamma.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import it.chalmers.gamma.db.entity.FKITGroup;
import it.chalmers.gamma.db.entity.FKITSuperGroup;
import it.chalmers.gamma.db.entity.ITUser;
import it.chalmers.gamma.db.entity.Membership;
import it.chalmers.gamma.db.serializers.FKITGroupSerializer;
import it.chalmers.gamma.db.serializers.ITUserSerializer;
import it.chalmers.gamma.domain.dto.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.FKITMinifiedGroupDTO;
import it.chalmers.gamma.domain.dto.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.ITUserDTO;
import it.chalmers.gamma.domain.dto.MembershipDTO;
import it.chalmers.gamma.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.group.GetActiveFKITGroupsResponse;
import it.chalmers.gamma.response.group.GetFKITGroupResponse;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.GroupWebsiteService;
import it.chalmers.gamma.service.MembershipService;
import it.chalmers.gamma.views.WebsiteDTO;

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
    public GetFKITGroupResponse getGroup(@PathVariable("id") String id) {
        final FKITGroupDTO group = this.getGroupByIdOrName(id);

        /* Retrieves all websites associated with a
           group ordered after website-type I.E. facebook pages */
        List<WebsiteDTO> websiteViews =
                this.groupWebsiteService.getWebsitesOrdered(
                        this.groupWebsiteService.getWebsites(group)
                );

        List<MembershipDTO> minifiedMembers = this.getMembershipDTO(group);

        return null;
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public List<FKITMinifiedGroupDTO> getGroupsMinified() {
        return this.fkitGroupService.getGroups().stream().map(FKITGroupDTO::toMinifiedDTO).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public FKITMinifiedGroupDTO getGroupMinified(@PathVariable("id") String id) {
        return this.getGroupByIdOrName(id).toMinifiedDTO();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<FKITGroupDTO> getGroups() {
        return this.fkitGroupService.getGroups();
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public GetActiveFKITGroupsResponse getActiveGroups() {
        List<FKITGroupDTO> groups = this.fkitGroupService.getGroups().stream()
                .filter(FKITGroupDTO::isActive).collect(Collectors.toList());

        for (FKITGroupDTO group : groups) {
            List<ITUserDTO> members = this.membershipService.getUsersInGroup(group);

            List<FKITSuperGroupDTO> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
        }

        //TODO
        return null;
    }

    private FKITGroupDTO getGroupByIdOrName(String idOrName) throws GroupDoesNotExistResponse {
        try {
            return Optional.ofNullable(this.fkitGroupService.getGroup(idOrName))
                    .or(() -> Optional.ofNullable(this.fkitGroupService.getGroup(UUID.fromString(idOrName))))
                    .orElseThrow(GroupDoesNotExistResponse::new);
        }
        catch (IllegalArgumentException e) {
            throw new GroupDoesNotExistResponse();
        }
    }

    private List<MembershipDTO> getMembershipDTO(FKITGroupDTO g) {
        return this.membershipService.getUsersInGroup(g).stream().map(user -> {
            MembershipDTO membership = this.membershipService.getMembershipByUserAndGroup(user, g);
            return new MembershipDTO(membership.getId().getPost(),
                    membership.getUnofficialPostName(),
                    membership.getId().getITUser().toDTO());
        }).collect(Collectors.toList());
    }

    private List<FKITSuperGroupDTO> getSuperGroupsDTO(FKITGroupDTO g) {
        return this.fkitGroupToSuperGroupService.getSuperGroups(g);
    }

}
