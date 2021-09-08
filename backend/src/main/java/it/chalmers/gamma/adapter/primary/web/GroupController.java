package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.GroupFacade;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.Group;

import java.util.List;

import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/groups")
public final class GroupController {

    private final GroupFacade groupFacade;

    public GroupController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }

    @GetMapping()
    public List<Group> getGroups() {
        return this.groupFacade.getAll();
    }

    @GetMapping("/{id}")
    public Group getGroup(@PathVariable("id") GroupId id) {
        return this.groupFacade.get(id).orElseThrow(GroupNotFoundResponse::new);
    }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

}
