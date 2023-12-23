package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {

    @GetMapping("/")
    public ModelAndView getHome(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        var mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/home");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "home");
        }

        return mv;
    }

    @GetMapping("/users")
    public ModelAndView getUsers(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        var mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/show-users");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "show-users");
        }

        return mv;
    }

}
