package it.chalmers.gamma.internal.membership.controller;

import it.chalmers.gamma.domain.GroupId;
import it.chalmers.gamma.internal.membership.service.MembershipDTO;
import it.chalmers.gamma.internal.membership.service.MembershipService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/{groupId}/members")
    public List<MembershipDTO> getMembersOfGroup(@PathVariable("groupId") GroupId groupId) {
        return this.membershipService.getMembershipsInGroup(groupId);
    }

}
