package it.chalmers.gamma.membership.controller;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.membership.controller.response.GetMembershipResponse;
import it.chalmers.gamma.membership.controller.response.GetMembershipResponse.GetMembershipResponseObject;

import java.util.List;
import java.util.stream.Collectors;

import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class MembershipController {

    private final GroupService groupService;
    private final MembershipService membershipService;

    public MembershipController(GroupService groupService, MembershipService membershipService) {
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @GetMapping("/{id}/members")
    public GetMembershipResponseObject getUsersInGroup(@PathVariable("id") String id) {
        GroupDTO group = this.groupService.getGroup(id);
        List<MembershipDTO> members = this.membershipService.getMembershipsInGroup(group);
        return new GetMembershipResponse(
                members
                    .stream()
                    .map(MembershipRestrictedDTO::new)
                    .collect(Collectors.toList())
        ).toResponseObject();
    }
}
