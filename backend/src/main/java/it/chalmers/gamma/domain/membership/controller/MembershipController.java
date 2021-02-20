package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.service.GroupService;
import it.chalmers.gamma.domain.membership.controller.response.GetMembershipResponse;
import it.chalmers.gamma.domain.membership.controller.response.GetMembershipResponse.GetMembershipResponseObject;

import java.util.UUID;

import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.membership.service.MembershipService;
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
    public GetMembershipResponseObject getUsersInGroup(@PathVariable("id") GroupId id) {
        return new GetMembershipResponse(
                this.membershipFinder.getRestrictedMembershipsInGroup(id)
        ).toResponseObject();
    }
}
