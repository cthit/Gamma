package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.group.GroupFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class GroupsController {

    private final GroupFacade groupFacade;

    public GroupsController(GroupFacade groupFacade) {
        this.groupFacade = groupFacade;
    }


    @GetMapping("/groups")
    public ModelAndView getGroups(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<GroupFacade.GroupDTO> groups = this.groupFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("groups");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/groups");
        }

        mv.addObject("groups", groups);

        return mv;
    }

}
