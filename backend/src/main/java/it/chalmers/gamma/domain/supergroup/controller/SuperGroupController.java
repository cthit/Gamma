package it.chalmers.gamma.domain.supergroup.controller;

import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.domain.group.service.GroupMinifiedDTO;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupFinder;

import java.util.List;

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

    public SuperGroupController(SuperGroupFinder superGroupFinder,
                                GroupFinder groupFinder) {
        this.superGroupFinder = superGroupFinder;
        this.groupFinder = groupFinder;
    }

    @GetMapping()
    public GetAllSuperGroupResponse getAllSuperGroups() {
        return new GetAllSuperGroupResponse(this.superGroupFinder.getAll());
    }

    @GetMapping("/{id}")
    public GetSuperGroupResponse getSuperGroup(@PathVariable("id") SuperGroupId id) {
        List<GroupMinifiedDTO> groups;

        try {
            groups = groupFinder.getGroupsMinifiedBySuperGroup(id);
            return new GetSuperGroupResponse(this.superGroupFinder.get(id), groups);
        } catch (EntityNotFoundException e) {
            LOGGER.error("SUPER GROUP NOT FOUND", e);
            throw new SuperGroupDoesNotExistResponse();
        }
    }
}
