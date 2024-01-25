package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.UserCreationFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterAccountController {

    private final UserCreationFacade userCreationFacade;

    public RegisterAccountController(UserCreationFacade userCreationFacade) {
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

    @PostMapping("/register")
    public ModelAndView registerAccount(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, CreateAccountForm form) {
        //TODO: Move validation to facade
        if(!form.password.equals(form.confirmPassword)) {

        }

        if(!form.acceptUserAgreement) {

        }

        try {
            this.userCreationFacade.createUserWithCode(
                    new UserCreationFacade.NewUser(
                            form.password,
                            form.nick,
                            form.firstName,
                            form.lastName,
                            form.email,
                            form.acceptanceYear,
                            form.cid,
                            form.language
                    ),
                    form.code
            );
        } catch (UserCreationFacade.SomePropertyNotUniqueException e) {
            throw new RuntimeException(e);
        }

        return new ModelAndView("redirect:/login");
    }

    public record CreateAccountForm(
            String cid,
            String code,
            String password,
            String confirmPassword,
            String nick,
            String firstName,
            String lastName,
            String email,
            int acceptanceYear,
            String language,
            boolean acceptUserAgreement
    ) {

    }

}
