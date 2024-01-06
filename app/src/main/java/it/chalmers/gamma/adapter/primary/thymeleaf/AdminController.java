package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.admin.AdminFacade;
import it.chalmers.gamma.app.user.UserFacade;
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
public class AdminController {

    private final UserFacade userFacade;
    private final AdminFacade adminFacade;

    public AdminController(UserFacade userFacade, AdminFacade adminFacade) {
        this.userFacade = userFacade;
        this.adminFacade = adminFacade;
    }

    @GetMapping("/admins")
    public ModelAndView getAllAdmins(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<UserFacade.UserDTO> users = this.userFacade.getAll();
        List<UUID> admins = this.adminFacade.getAllAdmins();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/admins");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/admins");
        }

        mv.addObject("users", users);
        mv.addObject("admins", admins);

        return mv;
    }

    @PutMapping("/admins/{userId}")
    public ModelAndView setAdmin(@RequestBody MultiValueMap<String, String> formData) {
        var entrySet = formData.entrySet().stream().findFirst();

        if(entrySet.isEmpty()) {
            throw new IllegalArgumentException("Unexpected form value");
        }

        UUID userId = UUID.fromString(entrySet.get().getKey());
        boolean shouldBeAdmin = entrySet.get().getValue().getFirst().equals("on");

        this.adminFacade.setAdmin(userId, shouldBeAdmin);

        return this.getAllAdmins(true);
    }

}
