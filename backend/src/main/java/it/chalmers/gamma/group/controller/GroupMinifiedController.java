package it.chalmers.gamma.group.controller;

import it.chalmers.gamma.group.controller.response.GetAllGroupsMinifiedResponse;
import it.chalmers.gamma.group.controller.response.GetGroupMinifiedResponse;
import it.chalmers.gamma.group.controller.response.GroupDoesNotExistResponse;
import it.chalmers.gamma.group.exception.GroupNotFoundException;
import it.chalmers.gamma.group.service.GroupFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/groups/minified")
public class GroupMinifiedController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupMinifiedController.class);

    private final GroupFinder groupFinder;

    public GroupMinifiedController(GroupFinder groupFinder) {
        this.groupFinder = groupFinder;
    }

    @GetMapping()
    public GetAllGroupsMinifiedResponse.GetAllGroupsMinifiedResponseObject getGroupsMinified() {
        return new GetAllGroupsMinifiedResponse(
                this.groupFinder.getGroupsMinified()
        ).toResponseObject();
    }

    @GetMapping("/{id}")
    public GetGroupMinifiedResponse.GetGroupMinifiedResponseObject getGroupMinified(@PathVariable("id") UUID id) {
        try {
            return new GetGroupMinifiedResponse(this.groupFinder.getGroupMinified(id)).toResponseObject();
        } catch (GroupNotFoundException e) {
            LOGGER.error("GROUP_NOT_FOUND", e);
            throw new GroupDoesNotExistResponse();
        }
    }

}
