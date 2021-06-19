package it.chalmers.gamma.internal.supergroup.controller;

import it.chalmers.gamma.domain.Group;
import it.chalmers.gamma.internal.group.service.GroupService;
import it.chalmers.gamma.domain.SuperGroup;
import it.chalmers.gamma.domain.SuperGroupId;

import java.util.List;

import it.chalmers.gamma.internal.supergroup.service.SuperGroupService;
import it.chalmers.gamma.util.response.ErrorResponse;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/superGroups")
public class SuperGroupController {

    private final SuperGroupService superGroupService;
    private final GroupService groupService;

    public SuperGroupController(SuperGroupService superGroupService,
                                GroupService groupService) {
        this.superGroupService = superGroupService;
        this.groupService = groupService;
    }

    @GetMapping()
    public List<SuperGroup> getAllSuperGroups() {
        return this.superGroupService.getAll();
    }

    @GetMapping("/{id}")
    public SuperGroup getSuperGroup(@PathVariable("id") SuperGroupId id) {
        try {
            return this.superGroupService.get(id);
        } catch (SuperGroupService.SuperGroupNotFoundException e) {
            throw new SuperGroupDoesNotExistResponse();
        }
    }

    @GetMapping("/{id}/subgroups")
    public List<Group> getSuperGroupSubGroups(@PathVariable("id") SuperGroupId id) {
        return this.groupService.getGroupsBySuperGroup(id);
    }

    private static class SuperGroupDoesNotExistResponse extends NotFoundResponse { }

}
