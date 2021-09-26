package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.domain.supergroup.SuperGroup;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import it.chalmers.gamma.util.response.NotFoundResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/superGroups")
public class SuperGroupController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
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

    private static class SuperGroupDoesNotExistResponse extends NotFoundResponse { }

}
