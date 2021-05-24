package it.chalmers.gamma.internal.supergroup.controller;

import it.chalmers.gamma.internal.supergroup.service.SuperGroupDTO;
import it.chalmers.gamma.util.domain.abstraction.exception.EntityNotFoundException;
import it.chalmers.gamma.internal.group.service.GroupMinifiedDTO;
import it.chalmers.gamma.internal.group.service.GroupFinder;
import it.chalmers.gamma.domain.SuperGroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupFinder;

import java.util.List;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/superGroups")
public class SuperGroupController {

    private final SuperGroupFinder superGroupFinder;
    private final GroupFinder groupFinder;

    public SuperGroupController(SuperGroupFinder superGroupFinder,
                                GroupFinder groupFinder) {
        this.superGroupFinder = superGroupFinder;
        this.groupFinder = groupFinder;
    }

    @GetMapping()
    public List<SuperGroupDTO> getAllSuperGroups() {
        return this.superGroupFinder.getAll();
    }

    @GetMapping("/{id}")
    public SuperGroupDTO getSuperGroup(@PathVariable("id") SuperGroupId id) {
        try {
            return this.superGroupFinder.get(id);
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    @GetMapping("/{id}/subgroups")
    public List<GroupMinifiedDTO> getSuperGroupSubGroups(@PathVariable("id") SuperGroupId id) {
        try {
            return this.groupFinder.getGroupsMinifiedBySuperGroup(id);
        } catch (EntityNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    private static class SuperGroupDoesNotExistResponse extends ErrorResponse {
        private SuperGroupDoesNotExistResponse() {
            super(HttpStatus.NOT_FOUND);
        }
    }

}
