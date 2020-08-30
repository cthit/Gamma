package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.RestrictedMembershipDTO;
import it.chalmers.gamma.response.group.GetActiveFKITGroupsResponse;
import it.chalmers.gamma.response.group.GetActiveFKITGroupsResponse.GetActiveFKITGroupResponseObject;
import it.chalmers.gamma.response.group.GetAllFKITGroupsMinifiedResponse;
import it.chalmers.gamma.response.group.GetAllFKITGroupsMinifiedResponse.GetAllFKITGroupsMinifiedResponseObject;
import it.chalmers.gamma.response.group.GetFKITGroupMinifiedResponse;
import it.chalmers.gamma.response.group.GetFKITGroupResponse;
import it.chalmers.gamma.response.group.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.supergroup.GetAllSuperGroupsResponse;
import it.chalmers.gamma.response.supergroup.GetAllSuperGroupsResponse.GetAllSuperGroupsResponseObject;
import it.chalmers.gamma.response.supergroup.GetSuperGroupResponse;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.FKITSuperGroupService;
import it.chalmers.gamma.service.MembershipService;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private final FKITSuperGroupService fkitSuperGroupService;
    private final MembershipService membershipService;
    private final FKITGroupService fkitGroupService;

    public SuperGroupController(FKITSuperGroupService fkitSuperGroupService,
                                MembershipService membershipService, FKITGroupService fkitGroupService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.membershipService = membershipService;
        this.fkitGroupService = fkitGroupService;
    }

    @GetMapping("/{id}/subgroups")
    public GetAllFKITGroupsMinifiedResponseObject getAllSubGroups(@PathVariable("id") String id) {
        FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(id);
        return new GetAllFKITGroupsMinifiedResponse(
                this.fkitGroupService.getAllGroupsWithSuperGroup(superGroup).stream()
                        .map(g -> new GetFKITGroupMinifiedResponse(g.toMinifiedDTO()))
                        .collect(Collectors.toList())).toResponseObject();
    }

    @GetMapping()
    public GetAllSuperGroupsResponseObject getAllSuperGroups() {
        return new GetAllSuperGroupsResponse(this.fkitSuperGroupService.getAllGroups()
                .stream().map(GetSuperGroupResponse::new).collect(Collectors.toList())).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetSuperGroupResponse getSuperGroup(@PathVariable("id") String id) {
        if (!this.fkitSuperGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        return new GetSuperGroupResponse(this.fkitSuperGroupService.getGroupDTO(id));
    }

    @GetMapping("/{id}/active")
    public GetActiveFKITGroupResponseObject getActiveGroup(@PathVariable("id") String id) {
        FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(id);
        List<GetFKITGroupResponse> groups = this.fkitGroupService.getActiveGroups(superGroup)
                .stream().map(g -> new GetFKITGroupResponse(
                        g,
                        this.membershipService.getMembershipsInGroup(g)
                                .stream()
                                .map(RestrictedMembershipDTO::new)
                                .collect(Collectors.toList())
            )).collect(Collectors.toList());
        return new GetActiveFKITGroupsResponse(groups).toResponseObject();
    }
}
