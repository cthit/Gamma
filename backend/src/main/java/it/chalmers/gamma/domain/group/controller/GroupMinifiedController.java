package it.chalmers.gamma.domain.group.controller;

import it.chalmers.gamma.domain.EntityNotFoundException;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.group.controller.response.GetAllGroupMinifiedResponse;
import it.chalmers.gamma.domain.group.controller.response.GetGroupMinifiedResponse;
import it.chalmers.gamma.domain.group.controller.response.GroupNotFoundResponse;
import it.chalmers.gamma.domain.group.service.GroupFinder;
import it.chalmers.gamma.domain.group.service.GroupMinifiedFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/groups/minified")
public class GroupMinifiedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMinifiedController.class);

    private final GroupMinifiedFinder groupMinifiedFinder;

    public GroupMinifiedController(GroupMinifiedFinder groupMinifiedFinder) {
        this.groupMinifiedFinder = groupMinifiedFinder;
    }

    @GetMapping()
    public GetAllGroupMinifiedResponse getGroupsMinified() {
        return new GetAllGroupMinifiedResponse(
                this.groupMinifiedFinder.getAll()
        );
    }

    @GetMapping("/{id}")
    public GetGroupMinifiedResponse getGroupMinified(@PathVariable("id") GroupId id) {
        try {
            return new GetGroupMinifiedResponse(this.groupMinifiedFinder.get(id));
        } catch (EntityNotFoundException e) {
            throw new GroupNotFoundResponse();
        }
    }

}
