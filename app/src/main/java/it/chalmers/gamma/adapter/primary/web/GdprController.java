package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
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
        List<UUID> gdprTrained = this.userGdprTrainingFacade.getGdprTrained();
        List<UserFacade.UserDTO> users = this.userFacade
                .getAll()
                .stream()
                .sorted((user1, user2) -> {
                    boolean user1Trained = gdprTrained.contains(user1.id());
                    boolean user2Trained = gdprTrained.contains(user2.id());

                    if(user1Trained && !user2Trained) {
                        return -1;
                    } else if(!user1Trained && user2Trained) {
                        return 1;
                    }

                    return user1.nick().compareToIgnoreCase(user2.nick());
                })
                .toList();

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

    @PutMapping("/gdpr")
    public ModelAndView setGdprTrained(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @RequestParam Map<String, String> form) {
        List<UUID> gdprTrained = this.userGdprTrainingFacade.getGdprTrained();
        List<UUID> newGdprTrained = form
                .keySet()
                .stream()
                .filter(s -> !("_csrf".equals(s) || "_method".equals(s)))
                .map(UUID::fromString)
                .toList();

        List<UUID> newlyTrained = newGdprTrained.stream().filter(userId -> !gdprTrained.contains(userId)).toList();
        List<UUID> noLongerTrained = gdprTrained.stream().filter(userId -> !newGdprTrained.contains(userId)).toList();

        for (UUID userId : newlyTrained) {
            this.userGdprTrainingFacade.updateGdprTrainedStatus(userId, true);
        }

        for (UUID userId : noLongerTrained) {
            this.userGdprTrainingFacade.updateGdprTrainedStatus(userId, false);
        }

        return new ModelAndView("redirect:gdpr");
    }

}
