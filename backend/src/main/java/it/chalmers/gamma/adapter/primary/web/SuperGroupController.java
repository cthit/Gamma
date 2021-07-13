package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.domain.Group;
import it.chalmers.gamma.app.group.GroupService;
import it.chalmers.gamma.app.domain.SuperGroup;
import it.chalmers.gamma.app.domain.SuperGroupId;

import java.util.List;

import it.chalmers.gamma.app.supergroup.SuperGroupService;
import it.chalmers.gamma.util.response.NotFoundResponse;
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
