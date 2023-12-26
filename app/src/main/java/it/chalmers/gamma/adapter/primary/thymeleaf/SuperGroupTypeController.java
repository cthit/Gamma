package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.supergroup.SuperGroupFacade;
import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class SuperGroupTypeController {

    private final SuperGroupFacade superGroupFacade;

    public SuperGroupTypeController(SuperGroupFacade superGroupFacade) {
        this.superGroupFacade = superGroupFacade;
    }

    @GetMapping("/types")
    public ModelAndView getSuperGroupTypes(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<String> types = this.superGroupFacade.getAllTypes();

        var mv = Page.SHOW_SUPER_GROUP_TYPES.create(htmxRequest);
        mv.addObject("types", types);

        return mv;
    }

}
