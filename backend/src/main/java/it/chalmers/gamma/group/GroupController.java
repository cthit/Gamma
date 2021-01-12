package it.chalmers.gamma.group;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.chalmers.gamma.membership.MembershipDTO;
import it.chalmers.gamma.membership.NoAccountMembershipDTO;
import it.chalmers.gamma.membership.RestrictedMembershipDTO;
import it.chalmers.gamma.membership.MembershipService;
import it.chalmers.gamma.group.response.GetActiveFKITGroupsResponse;
import it.chalmers.gamma.group.response.GetActiveFKITGroupsResponse.GetActiveFKITGroupResponseObject;
import it.chalmers.gamma.group.response.GetAllFKITGroupsMinifiedResponse;
import it.chalmers.gamma.group.response.GetAllFKITGroupsMinifiedResponse.GetAllFKITGroupsMinifiedResponseObject;
import it.chalmers.gamma.group.response.GetAllFKITGroupsResponse;
import it.chalmers.gamma.group.response.GetFKITGroupMinifiedResponse;
import it.chalmers.gamma.group.response.GetFKITGroupMinifiedResponse.GetFKITGroupMinifiedResponseObject;
import it.chalmers.gamma.group.response.GetFKITGroupResponse;
import it.chalmers.gamma.group.response.GetFKITGroupResponse.GetFKITGroupResponseObject;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressFBWarnings(justification = "I don't know", value = "UC_USELESS_OBJECT")
@SuppressWarnings("PMD.ExcessiveImports")
@RestController
@RequestMapping("/groups")
public final class GroupController {

    //TODO add groupmembers to serialize method call once that has been solved.

    private final GroupService groupService;
    private final MembershipService membershipService;

    public GroupController(
            GroupService groupService,
            MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @GetMapping("/{id}")
    public GetFKITGroupResponseObject getGroup(@PathVariable("id") String id) {
        final FKITGroupDTO group = this.groupService.getGroup(id);
        List<MembershipDTO> minifiedMembers = this.membershipService.getMembershipsInGroup(group);
        List<NoAccountMembershipDTO> noAccountMemberships = this.membershipService.getNoAccountMembership(group);
        return new GetFKITGroupResponse(
                group,
                toRestrictedMembershipDTO(minifiedMembers),
                noAccountMemberships)
            .toResponseObject();
    }

    @GetMapping("/minified")
    public GetAllFKITGroupsMinifiedResponseObject getGroupsMinified() {
        List<GetFKITGroupMinifiedResponse> responses = this.groupService.getGroups()
                .stream().map(g -> new GetFKITGroupMinifiedResponse(g.toMinifiedDTO())).collect(Collectors.toList());
        return new GetAllFKITGroupsMinifiedResponse(responses).toResponseObject();
    }

    @GetMapping("/{id}/minified")
    public GetFKITGroupMinifiedResponseObject getGroupMinified(@PathVariable("id") String id) {
        return new GetFKITGroupMinifiedResponse(this.groupService.getGroup(id)
                .toMinifiedDTO()).toResponseObject();
    }

    @GetMapping()
    public GetAllFKITGroupsResponse getGroups() {
        List<GetFKITGroupResponse> responses = this.groupService.getGroups()
                .stream().map(g -> new GetFKITGroupResponse(
                        g,
                        toRestrictedMembershipDTO(this.membershipService.getMembershipsInGroup(g)),
                        this.membershipService.getNoAccountMembership(g)
                )).collect(Collectors.toList());

        return new GetAllFKITGroupsResponse(responses);
    }

    @GetMapping("/active")
    public GetActiveFKITGroupResponseObject getActiveGroups() {
        List<FKITGroupDTO> groups = this.groupService.getGroups().stream()
                .filter(FKITGroupDTO::isActive).collect(Collectors.toList());

        List<GetFKITGroupResponse> groupResponses = groups.stream().map(g -> new GetFKITGroupResponse(
                g,
                toRestrictedMembershipDTO(this.membershipService.getMembershipsInGroup(g)),
                this.membershipService.getNoAccountMembership(g)
        )).collect(Collectors.toList());
        return new GetActiveFKITGroupsResponse(groupResponses).toResponseObject();
    }

    private List<RestrictedMembershipDTO> toRestrictedMembershipDTO(List<MembershipDTO> membershipList) {
        return membershipList
                .stream()
                .map(RestrictedMembershipDTO::new)
                .collect(Collectors.toList());
    }

}
