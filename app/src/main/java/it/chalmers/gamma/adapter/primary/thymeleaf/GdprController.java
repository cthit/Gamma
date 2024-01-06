package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class GdprController {

    private final UserFacade userFacade;
    private final UserGdprTrainingFacade userGdprTrainingFacade;

    public GdprController(UserFacade userFacade, UserGdprTrainingFacade userGdprTrainingFacade) {
        this.userFacade = userFacade;
        this.userGdprTrainingFacade = userGdprTrainingFacade;
    }

    @GetMapping("/gdpr")
    public ModelAndView getAlLGdprTrained(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<UserFacade.UserDTO> users = this.userFacade.getAll();
        List<UUID> gdprTrained = this.userGdprTrainingFacade.getGdprTrained();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/gdpr");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/gdpr");
        }

        mv.addObject("users", users);
        mv.addObject("gdprTrained", gdprTrained);

        return mv;
    }

    @PutMapping("/gdpr/{userId}")
    public ModelAndView seGdprTrained(@RequestBody MultiValueMap<String, String> formData) {
        var entrySet = formData.entrySet().stream().findFirst();

        if(entrySet.isEmpty()) {
            throw new IllegalArgumentException("Unexpected form value");
        }

        UUID userId = UUID.fromString(entrySet.get().getKey());
        boolean haveBeenGdprTrained = entrySet.get().getValue().getFirst().equals("on");

        this.userGdprTrainingFacade.updateGdprTrainedStatus(userId, haveBeenGdprTrained);

        return this.getAlLGdprTrained(true);
    }


}
