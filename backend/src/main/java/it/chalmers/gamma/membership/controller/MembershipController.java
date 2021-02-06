package it.chalmers.gamma.membership.controller;

import it.chalmers.gamma.group.dto.GroupDTO;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.membership.controller.response.GetMembershipResponse;
import it.chalmers.gamma.membership.controller.response.GetMembershipResponse.GetMembershipResponseObject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import it.chalmers.gamma.membership.dto.MembershipDTO;
import it.chalmers.gamma.membership.service.MembershipFinder;
import it.chalmers.gamma.membership.service.MembershipService;
import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class MembershipController {

    private final MembershipFinder membershipFinder;
    private final GroupService groupService;
    private final MembershipService membershipService;

    public MembershipController(MembershipFinder membershipFinder,
                                GroupService groupService,
                                MembershipService membershipService) {
        this.membershipFinder = membershipFinder;
        this.groupService = groupService;
        this.membershipService = membershipService;
    }

    @GetMapping("/{id}/members")
    public GetMembershipResponseObject getUsersInGroup(@PathVariable("id") UUID id) {
        return new GetMembershipResponse(
                this.membershipFinder.getRestrictedMembershipsInGroup(id)
        ).toResponseObject();
    }
}
