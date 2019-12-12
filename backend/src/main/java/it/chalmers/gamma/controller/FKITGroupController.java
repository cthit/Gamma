package it.chalmers.gamma.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.response.group.GetAllFKITGroupsResponse;
import it.chalmers.gamma.response.group.GetActiveFKITGroupsResponse;
import it.chalmers.gamma.response.group.GetActiveFKITGroupsResponse.GetActiveFKITGroupResponseObject;
import it.chalmers.gamma.response.group.GetAllFKITGroupsMinifiedResponse;
import it.chalmers.gamma.response.group.GetAllFKITGroupsMinifiedResponse.GetAllFKITGroupsMinifiedResponseObject;
import it.chalmers.gamma.response.group.GetFKITGroupMinifiedResponse;
import it.chalmers.gamma.response.group.GetFKITGroupMinifiedResponse.GetFKITGroupMinifiedResponseObject;
import it.chalmers.gamma.response.group.GetFKITGroupResponse;
import it.chalmers.gamma.response.group.GetFKITGroupResponse.GetFKITGroupResponseObject;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITGroupToSuperGroupService;
import it.chalmers.gamma.service.MembershipService;

import java.util.List;
import java.util.stream.Collectors;

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
    private final MembershipService membershipService;
    private final FKITGroupToSuperGroupService fkitGroupToSuperGroupService;

    public FKITGroupController(
            FKITGroupService fkitGroupService,
            MembershipService membershipService,
            FKITGroupToSuperGroupService fkitGroupToSuperGroupService) {
        this.fkitGroupService = fkitGroupService;
        this.membershipService = membershipService;
        this.fkitGroupToSuperGroupService = fkitGroupToSuperGroupService;
    }

    @GetMapping("/{id}")
    public GetFKITGroupResponseObject getGroup(@PathVariable("id") String id) {
        final FKITGroupDTO group = this.fkitGroupService.getDTOGroup(id);
        List<MembershipDTO> minifiedMembers = this.membershipService.getMembershipsInGroup(group);
        List<FKITSuperGroupDTO> superGroups = this.fkitGroupToSuperGroupService.getSuperGroups(group);
        //List<WebsiteDTO> websites = this.getWebsiteDTO(group);
        return new GetFKITGroupResponse(group, minifiedMembers, superGroups, null).toResponseObject();
    }

    @RequestMapping(value = "/minified", method = RequestMethod.GET)
    public GetAllFKITGroupsMinifiedResponseObject getGroupsMinified() {
        List<GetFKITGroupMinifiedResponse> responses = this.fkitGroupService.getGroups()
                .stream().map(g -> new GetFKITGroupMinifiedResponse(g.toMinifiedDTO())).collect(Collectors.toList());
        return new GetAllFKITGroupsMinifiedResponse(responses).toResponseObject();
    }

    @RequestMapping(value = "/{id}/minified", method = RequestMethod.GET)
    public GetFKITGroupMinifiedResponseObject getGroupMinified(@PathVariable("id") String id) {
        return new GetFKITGroupMinifiedResponse(this.fkitGroupService.getDTOGroup(id).toMinifiedDTO()).toResponseObject();
    }

    @RequestMapping(method = RequestMethod.GET)
    public GetAllFKITGroupsResponse getGroups() {
        List<GetFKITGroupResponse> responses = this.fkitGroupService.getGroups()
                .stream().map(g -> new GetFKITGroupResponse(
                        g,
                        this.membershipService.getMembershipsInGroup(g),
                        this.fkitGroupToSuperGroupService.getSuperGroups(g),
                        null
                )).collect(Collectors.toList());

        return new GetAllFKITGroupsResponse(responses);
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public GetActiveFKITGroupResponseObject getActiveGroups() {
        List<FKITGroupDTO> groups = this.fkitGroupService.getGroups().stream()
                .filter(FKITGroupDTO::isActive).collect(Collectors.toList());

        List<GetFKITGroupResponse> groupResponses = groups.stream().map(g -> new GetFKITGroupResponse(
                g,
                this.membershipService.getMembershipsInGroup(g),
                this.fkitGroupToSuperGroupService.getSuperGroups(g),
                null
        )).collect(Collectors.toList());
        return new GetActiveFKITGroupsResponse(groupResponses).toResponseObject();
    }

}
