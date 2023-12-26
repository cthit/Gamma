package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    @GetMapping("/")
    public ModelAndView getHome(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
       return Page.HOME.create(htmxRequest);
    }

}
