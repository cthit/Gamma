package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class SuperGroupController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/super-groups")
    public ModelAndView getSuperGroups(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<SuperGroupFacade.SuperGroupDTO> superGroups = this.superGroupFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/show-super-groups");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/show-super-groups");
        }

        mv.addObject("superGroups", superGroups);

        return mv;
    }

}