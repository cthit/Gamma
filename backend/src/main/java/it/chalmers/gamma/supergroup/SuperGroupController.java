package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.membership.RestrictedMembershipDTO;
import it.chalmers.gamma.group.response.GetActiveFKITGroupsResponse;
import it.chalmers.gamma.group.response.GetActiveFKITGroupsResponse.GetActiveFKITGroupResponseObject;
import it.chalmers.gamma.group.response.GetAllFKITGroupsMinifiedResponse;
import it.chalmers.gamma.group.response.GetAllFKITGroupsMinifiedResponse.GetAllFKITGroupsMinifiedResponseObject;
import it.chalmers.gamma.group.response.GetFKITGroupMinifiedResponse;
import it.chalmers.gamma.group.response.GetFKITGroupResponse;
import it.chalmers.gamma.group.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse.GetAllSuperGroupsResponseObject;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse;
import it.chalmers.gamma.group.GroupService;
import it.chalmers.gamma.membership.MembershipService;

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
    private final GroupService groupService;

    public SuperGroupController(FKITSuperGroupService fkitSuperGroupService,
                                MembershipService membershipService, GroupService groupService) {
        this.fkitSuperGroupService = fkitSuperGroupService;
        this.membershipService = membershipService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}/subgroups")
    public GetAllFKITGroupsMinifiedResponseObject getAllSubGroups(@PathVariable("id") String id) {
        FKITSuperGroupDTO superGroup = this.fkitSuperGroupService.getGroupDTO(id);
        return new GetAllFKITGroupsMinifiedResponse(
                this.groupService.getAllGroupsWithSuperGroup(superGroup).stream()
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
        List<GetFKITGroupResponse> groups = this.groupService.getActiveGroups(superGroup)
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
