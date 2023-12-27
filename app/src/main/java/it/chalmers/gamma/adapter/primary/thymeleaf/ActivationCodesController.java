package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.activation.ActivationCodeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ActivationCodesController {

    private final ActivationCodeFacade activationCodeFacade;


    public ActivationCodesController(ActivationCodeFacade activationCodeFacade) {
        this.activationCodeFacade = activationCodeFacade;
    }

    @GetMapping("/activation-codes")
    public ModelAndView getActivationCodes(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<ActivationCodeFacade.UserActivationDTO> activationCodes = this.activationCodeFacade.getAllUserActivations();

        ModelAndView mv = Page.SHOW_ACTIVATION_CODES.create(htmxRequest);
        mv.addObject("activationCodes", activationCodes);

        return mv;
    }
}