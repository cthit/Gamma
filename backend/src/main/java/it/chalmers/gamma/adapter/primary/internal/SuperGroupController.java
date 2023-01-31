package it.chalmers.gamma.adapter.primary.internal;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/superGroups")
public class SuperGroupController {

    private final SuperGroupFacade superGroupFacade;
    private final GroupFacade groupFacade;

    public SuperGroupController(SuperGroupFacade superGroupFacade,
                                GroupFacade groupFacade) {
        this.superGroupFacade = superGroupFacade;
        this.groupFacade = groupFacade;
    }

    @GetMapping()
    public List<SuperGroupFacade.SuperGroupDTO> getAllSuperGroups() {
        return this.superGroupFacade.getAllSuperGroups();
    }

    @GetMapping("/{id}")
    public SuperGroupFacade.SuperGroupDTO getSuperGroup(@PathVariable("id") UUID id) {
        return this.superGroupFacade.get(id)
                .orElseThrow(SuperGroupDoesNotExistResponse::new);
    }

    @GetMapping("/{id}/subgroups")
    public List<GroupFacade.GroupWithMembersDTO> getSubgroups(@PathVariable("id") UUID id) {
        return this.groupFacade.getAllBySuperGroup(id);
    }

    private static class SuperGroupDoesNotExistResponse extends NotFoundResponse {
    }

}
