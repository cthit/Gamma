package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.MeFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class MyAcceptedClientsController {

    private final MeFacade meFacade;

    public MyAcceptedClientsController(MeFacade meFacade) {
        this.meFacade = meFacade;
    }

    @GetMapping("/me/accepted-clients")
    public ModelAndView getMyAcceptedClients(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<MeFacade.UserApprovedClientDTO> acceptedClients = this.meFacade.getSignedInUserApprovals();

        ModelAndView mv = new ModelAndView();

        if(htmxRequest) {
            mv.setViewName("pages/my-accepted-clients");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/my-accepted-clients");
        }

        mv.addObject("acceptedClients", acceptedClients);

        return mv;
    }

    @DeleteMapping("/me/accepted-clients/{uid}")
    public ModelAndView deleteAcceptedClient(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
                                             @PathVariable("uid") UUID clientUid) {
        this.meFacade.deleteUserApproval(clientUid);

        return new ModelAndView("partial/retracted-accepted-client");
    }

}
