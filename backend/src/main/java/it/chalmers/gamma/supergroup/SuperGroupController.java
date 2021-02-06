package it.chalmers.gamma.supergroup;

import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import it.chalmers.gamma.group.service.GroupFinder;
import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;
import it.chalmers.gamma.group.controller.response.GetActiveGroupsResponse;
import it.chalmers.gamma.group.controller.response.GetActiveGroupsResponse.GetActiveGroupResponseObject;
import it.chalmers.gamma.group.controller.response.GetAllGroupsMinifiedResponse;
import it.chalmers.gamma.group.controller.response.GetAllGroupsMinifiedResponse.GetAllGroupsMinifiedResponseObject;
import it.chalmers.gamma.group.controller.response.GetGroupResponse;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.supergroup.exception.SuperGroupNotFoundException;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse;
import it.chalmers.gamma.supergroup.response.GetAllSuperGroupsResponse.GetAllSuperGroupsResponseObject;
import it.chalmers.gamma.supergroup.response.GetGroupsBySuperGroupResponse;
import it.chalmers.gamma.supergroup.response.GetSuperGroupResponse;
import it.chalmers.gamma.group.service.GroupService;
import it.chalmers.gamma.membership.service.MembershipService;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import it.chalmers.gamma.supergroup.response.SuperGroupDoesNotExistResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperGroupController.class);

    private final SuperGroupFinder superGroupFinder;
    private final GroupFinder groupFinder;
    private final SuperGroupService superGroupService;
    private final MembershipService membershipService;
    private final GroupService groupService;

    public SuperGroupController(SuperGroupFinder superGroupFinder,
                                GroupFinder groupFinder, SuperGroupService superGroupService,
                                MembershipService membershipService,
                                GroupService groupService) {
        this.superGroupFinder = superGroupFinder;
        this.groupFinder = groupFinder;
        this.superGroupService = superGroupService;
        this.membershipService = membershipService;
        this.groupService = groupService;
    }

    @GetMapping("/{id}/subgroups")
    public GetGroupsBySuperGroupResponse.GetGroupsBySuperGroupResponseObject getGroupsBySuperGroup(@PathVariable("id") UUID id) {
        List<GroupMinifiedDTO> groups;

        try {
            groups = groupFinder.getGroupsMinifiedBySuperGroup(id);
        } catch (SuperGroupNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }

        return new GetGroupsBySuperGroupResponse(groups).toResponseObject();
    }

    @GetMapping()
    public GetAllSuperGroupsResponseObject getAllSuperGroups() {
        return new GetAllSuperGroupsResponse(this.superGroupFinder.getSuperGroups()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetSuperGroupResponse getSuperGroup(@PathVariable("id") UUID id) {
        try {
            return new GetSuperGroupResponse(this.superGroupFinder.getSuperGroup(id));
        } catch (SuperGroupNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    @GetMapping("/{id}/active")
    public GetActiveGroupResponseObject getActiveGroup(@PathVariable("id") UUID superGroupId) {
        try {
            return new GetActiveGroupsResponse(
                    this.groupFinder.getActiveGroupsWithMembersBySuperGroup(superGroupId)
            ).toResponseObject();
        } catch (SuperGroupNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }
    }
}
