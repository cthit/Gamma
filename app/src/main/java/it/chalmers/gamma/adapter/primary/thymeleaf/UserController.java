package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }


    @GetMapping("/users")
    public ModelAndView getUsers(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<UserFacade.UserDTO> users = this.userFacade.getAll();

        var mv = new ModelAndView();
        mv.addObject("users", users);

        if(htmxRequest) {
            mv.setViewName("pages/show-users");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "show-users");
        }

        return mv;
    }
}
