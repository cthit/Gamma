package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.group.GroupFacade;

import java.util.List;
import java.util.UUID;

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
    public List<GroupFacade.GroupDTO> getGroups() {
        return this.groupFacade.getAll();
    }

    @GetMapping("/{id}")
    public GroupFacade.GroupWithMembersDTO getGroup(@PathVariable("id") UUID id) {
        return this.groupFacade.getWithMembers(id).orElseThrow(GroupNotFoundResponse::new);
    }

    private static class GroupNotFoundResponse extends NotFoundResponse { }

}
