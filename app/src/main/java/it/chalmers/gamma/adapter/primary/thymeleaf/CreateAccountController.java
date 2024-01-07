package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.UserCreationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CreateAccountController {

    private final UserCreationFacade userCreationFacade;

    public CreateAccountController(UserCreationFacade userCreationFacade) {
        this.userCreationFacade = userCreationFacade;
    }

    @GetMapping("/activate-cid")
    public ModelAndView getActivateCid(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, String cidInputError) {
        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/activate-cid");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/activate-cid");
        }

        mv.addObject("cidInputError", cidInputError);

        return mv;
    }

    @PostMapping("/activate-cid")
    public ModelAndView activateCid(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, ActivateCidForm form) {
        try {
            this.userCreationFacade.tryToActivateUser(form.cid);
        } catch (IllegalArgumentException e) {
            return this.getActivateCid(htmxRequest, e.getMessage());
        }
        return new ModelAndView("redirect:email-sent");
    }

    public record ActivateCidForm (String cid) { }

    @GetMapping("/email-sent")
    public ModelAndView getEmailSent(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("pages/email-sent");

        return mv;
    }

    @GetMapping("/register")
    public ModelAndView getRegister(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/register-account");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/register-account");
        }
        return mv;
    }

}
