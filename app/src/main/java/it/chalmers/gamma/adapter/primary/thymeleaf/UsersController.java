package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.UserFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UsersController {

    private final UserFacade userFacade;

    public UsersController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/users")
    public ModelAndView getUsers(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
        List<UserFacade.UserDTO> users = this.userFacade.getAll();

        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/users");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/users");
        }

        mv.addObject("users", users);

        return mv;
    }

    @GetMapping("/users/{id}")
    public ModelAndView getUser(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @PathVariable("id") UUID userId) {
        Optional<UserFacade.UserWithGroupsDTO> user = this.userFacade.get(userId);

        if(user.isEmpty()) {
            throw new RuntimeException();
        }

        ModelAndView mv = new ModelAndView();
        if (htmxRequest) {
            mv.setViewName("pages/user-details");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/user-details");
        }

        UserFacade.UserWithGroupsDTO u = user.get();

        String name = u.user().firstName() + " '" + u.user().nick() + "' " + u.user().lastName();

        mv.addObject("userId", u.user().id());
        mv.addObject("name", name);
        mv.addObject("cid", u.user().cid());
        mv.addObject("acceptanceYear", u.user().acceptanceYear());
        mv.addObject("groups", u.groups()
                .stream()
                .map(userGroup -> userGroup.group().prettyName() + " - " + userGroup.post().enName())
                .toList()
        );

        return mv;
    }
}
