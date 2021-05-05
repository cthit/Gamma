package it.chalmers.gamma.domain.membership.controller;

import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.membership.service.MembershipRestrictedDTO;
import it.chalmers.gamma.domain.membership.service.MembershipRestrictedFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class MembershipController {

    private final MembershipRestrictedFinder membershipRestrictedFinder;

    public MembershipController(MembershipRestrictedFinder membershipRestrictedFinder) {
        this.membershipRestrictedFinder = membershipRestrictedFinder;
    }

    @GetMapping("/{groupId}/members")
    public List<MembershipRestrictedDTO> getMembersOfGroup(@PathVariable("groupId") GroupId groupId) {
        return this.membershipRestrictedFinder.getRestrictedMembershipsInGroup(groupId);
    }

}
