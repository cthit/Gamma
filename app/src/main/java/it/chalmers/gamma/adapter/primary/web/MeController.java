package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MeController {

    private final MeFacade meFacade;

    public MeController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @GetMapping("/me")
    public ModelAndView getMe(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/me");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/me");
        }

        MeFacade.MeDTO me = this.meFacade.getMe();

        mv.addObject("me", me);

        return mv;
    }

}
