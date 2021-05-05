package it.chalmers.gamma.internal.group.controller;

import it.chalmers.gamma.internal.group.service.GroupMinifiedDTO;
import it.chalmers.gamma.internal.group.service.GroupMinifiedFinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups/minified")
public class GroupMinifiedController {

    private final GroupMinifiedFinder groupMinifiedFinder;

    public GroupMinifiedController(GroupMinifiedFinder groupMinifiedFinder) {
        this.groupMinifiedFinder = groupMinifiedFinder;
    }

    @GetMapping()
    public List<GroupMinifiedDTO> getGroupsMinified() {
        return this.groupMinifiedFinder.getAll();
    }

}
