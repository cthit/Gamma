package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.group.data.GroupMinifiedDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.group.controller.response.GetActiveGroupResponse;
import it.chalmers.gamma.domain.group.service.GroupMinifiedFinder;
import it.chalmers.gamma.domain.membership.service.MembershipFinder;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;
import it.chalmers.gamma.domain.supergroup.controller.response.GetAllSuperGroupsResponse;
import it.chalmers.gamma.domain.supergroup.controller.response.GetAllSuperGroupsResponse.GetAllSuperGroupsResponseObject;
import it.chalmers.gamma.domain.supergroup.controller.response.GetGroupsBySuperGroupResponse;
import it.chalmers.gamma.domain.supergroup.controller.response.GetSuperGroupResponse;

import java.util.List;

import it.chalmers.gamma.domain.supergroup.controller.response.SuperGroupDoesNotExistResponse;
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
    private final MembershipFinder membershipFinder;
    private final GroupMinifiedFinder groupMinifiedFinder;

    public SuperGroupController(SuperGroupFinder superGroupFinder,
                                GroupFinder groupFinder,
                                MembershipFinder membershipFinder,
                                GroupMinifiedFinder groupMinifiedFinder) {
        this.superGroupFinder = superGroupFinder;
        this.groupFinder = groupFinder;
        this.membershipFinder = membershipFinder;
        this.groupMinifiedFinder = groupMinifiedFinder;
    }

    @GetMapping("/{id}/subgroups")
    public GetGroupsBySuperGroupResponse.GetGroupsBySuperGroupResponseObject getGroupsBySuperGroup(@PathVariable("id") SuperGroupId id) {
        List<GroupMinifiedDTO> groups;

        try {
            groups = groupFinder.getGroupsMinifiedBySuperGroup(id);
        } catch (EntityNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }

        return new GetGroupsBySuperGroupResponse(groups).toResponseObject();
    }

    @GetMapping()
    public GetAllSuperGroupsResponseObject getAllSuperGroups() {
        return new GetAllSuperGroupsResponse(this.superGroupFinder.getAll()).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetSuperGroupResponse getSuperGroup(@PathVariable("id") SuperGroupId id) {
        try {
            return new GetSuperGroupResponse(this.superGroupFinder.get(id));
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    @GetMapping("/{id}/active")
    public GetActiveGroupResponse getActiveGroup(@PathVariable("id") SuperGroupId superGroupId) {
        try {
            return new GetActiveGroupResponse(
                    this.membershipFinder.getActiveGroupsWithMembershipsBySuperGroup(superGroupId)
            );
        } catch (EntityNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }
    }
}
