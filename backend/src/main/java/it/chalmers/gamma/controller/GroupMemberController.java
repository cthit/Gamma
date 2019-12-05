package it.chalmers.gamma.controller;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.response.group.GetMembershipResponse;
import it.chalmers.gamma.response.group.GroupDoesNotExistResponse;
import it.chalmers.gamma.response.group.GetMembershipResponse.GetMembershipResponseObject;
import it.chalmers.gamma.service.FKITGroupService;
import it.chalmers.gamma.service.MembershipService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups")
public class GroupMemberController {

    private final FKITGroupService fkitGroupService;
    private final MembershipService membershipService;

    public GroupMemberController(FKITGroupService fkitGroupService, MembershipService membershipService) {
        this.fkitGroupService = fkitGroupService;
        this.membershipService = membershipService;
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    public GetMembershipResponseObject getUsersInGroup(@PathVariable("id") String id) {
        FKITGroupDTO group = this.fkitGroupService.getDTOGroup(id);
        List<MembershipDTO> members = this.membershipService.getMembershipsInGroup(group);
        return new GetMembershipResponse(members).getResponseObject();
    }
}
