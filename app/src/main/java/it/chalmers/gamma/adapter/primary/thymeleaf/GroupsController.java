package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.group.GroupFacade;
import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class GroupsController {

    private final GroupFacade groupFacade;
    private final SuperGroupFacade superGroupFacade;

    public GroupsController(GroupFacade groupFacade, SuperGroupFacade superGroupFacade) {
        this.groupFacade = groupFacade;
        this.superGroupFacade = superGroupFacade;
    }


    @GetMapping("/groups")
    public ModelAndView getGroups(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<GroupFacade.GroupDTO> groups = this.groupFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/groups");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/groups");
        }

        mv.addObject("groups", groups);

        return mv;
    }

    @GetMapping("/groups/{id}")
    public ModelAndView getGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<GroupFacade.GroupDTO> group = this.groupFacade.get(id);

        if (group.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/group-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/group-details");
        }

        mv.addObject("group", group.get());

        return mv;
    }

    public record UpdateGroupForm(int version,
                                  String name,
                                  String prettyName,
                                  UUID superGroupId) {
    }

    @GetMapping("/groups/{id}/edit")
    public ModelAndView getGroupEdit(@RequestHeader(value = "HX-Request", required = true) boolean htmxRequest, @PathVariable("id") UUID id) {
        Optional<GroupFacade.GroupDTO> group = this.groupFacade.get(id);

        if (group.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("partial/edit-group");

        List<SuperGroupFacade.SuperGroupDTO> superGroups = this.superGroupFacade.getAll();

        UpdateGroupForm form = new UpdateGroupForm(group.get().version(), group.get().name(), group.get().prettyName(), group.get().superGroup().id());
        mv.addObject("form", form);
        mv.addObject("superGroups", superGroups);
        mv.addObject("groupId", group.get().id());

        return mv;
    }

    @PutMapping("/groups/{id}")
    public ModelAndView updateGroup(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
                                    @PathVariable("id") UUID id,
                                    final UpdateGroupForm form,
                                    final BindingResult bindingResult) {
        try {
            this.groupFacade.update(new GroupFacade.UpdateGroup(
                    id,
                    form.version,
                    form.name,
                    form.prettyName,
                    form.superGroupId
            ));
        } catch (GroupFacade.GroupAlreadyExistsException e) {
            throw new RuntimeException(e);
        }

        ModelAndView mv = new ModelAndView();
        mv.setViewName("pages/group-details");
        mv.addObject("group", this.groupFacade.get(id).get());

        return mv;
    }
}