package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.UserCreationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CreateAccountController {

    private final UserCreationFacade userCreationFacade;

    public CreateAccountController(UserCreationFacade userCreationFacade) {
        this.userCreationFacade = userCreationFacade;
    }

    @GetMapping("/register")
    public ModelAndView getActivateCid(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/activate-cid");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/activate-cid");
        }
        return mv;
    }

}
