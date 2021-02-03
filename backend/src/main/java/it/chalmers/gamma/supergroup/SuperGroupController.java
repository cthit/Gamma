package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;
import it.chalmers.gamma.group.controller.response.GetActiveGroupsResponse;
import it.chalmers.gamma.group.controller.response.GetActiveGroupsResponse.GetActiveGroupResponseObject;
import it.chalmers.gamma.group.controller.response.GetAllGroupsMinifiedResponse;
import it.chalmers.gamma.group.controller.response.GetAllGroupsMinifiedResponse.GetAllGroupsMinifiedResponseObject;
import it.chalmers.gamma.group.controller.response.GetGroupMinifiedResponse;
import it.chalmers.gamma.group.controller.response.GetGroupResponse;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse.GetAllSuperGroupsResponseObject;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.membership.service.MembershipService;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private final SuperGroupService superGroupService;
    private final MembershipService membershipService;
    private final GroupService groupService;

    public SuperGroupController(SuperGroupService superGroupService,
                                MembershipService membershipService, GroupService groupService) {
        this.superGroupService = superGroupService;
        this.membershipService = membershipService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}/subgroups")
    public GetAllGroupsMinifiedResponseObject getAllSubGroups(@PathVariable("id") String id) {
        SuperGroupDTO superGroup = this.superGroupService.getGroupDTO(id);
        return new GetAllGroupsMinifiedResponse(
                this.groupService.getAllGroupsWithSuperGroup(superGroup).stream()
                        .map(g -> new GetGroupMinifiedResponse(g.toMinifiedDTO()))
                        .collect(Collectors.toList())).toResponseObject();
    }

    @GetMapping()
    public GetAllSuperGroupsResponseObject getAllSuperGroups() {
        return new GetAllSuperGroupsResponse(this.superGroupService.getAllGroups()
                .stream().map(GetSuperGroupResponse::new).collect(Collectors.toList())).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetSuperGroupResponse getSuperGroup(@PathVariable("id") String id) {
        if (!this.superGroupService.groupExists(id)) {
            throw new GroupDoesNotExistResponse();
        }
        return new GetSuperGroupResponse(this.superGroupService.getGroupDTO(id));
    }

    @GetMapping("/{id}/active")
    public GetActiveGroupResponseObject getActiveGroup(@PathVariable("id") String id) {
        SuperGroupDTO superGroup = this.superGroupService.getGroupDTO(id);
        List<GetGroupResponse> groups = this.groupService.getActiveGroups(superGroup)
                .stream().map(g -> new GetGroupResponse(
                        g,
                        this.membershipService.getMembershipsInGroup(g)
                                .stream()
                                .map(MembershipRestrictedDTO::new)
                                .collect(Collectors.toList())
            )).collect(Collectors.toList());
        return new GetActiveGroupsResponse(groups).toResponseObject();
    }
}
