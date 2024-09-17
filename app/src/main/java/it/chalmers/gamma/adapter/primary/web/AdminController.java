package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.admin.AdminFacade;
import it.chalmers.gamma.app.user.UserFacade;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

  private final UserFacade userFacade;
  private final AdminFacade adminFacade;

  public AdminController(UserFacade userFacade, AdminFacade adminFacade) {
    this.userFacade = userFacade;
    this.adminFacade = adminFacade;
  }

  @GetMapping("/admins")
  public ModelAndView getAdmins(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<UUID> admins = this.adminFacade.getAllAdmins();
    List<UserFacade.UserDTO> users =
        this.userFacade.getAll().stream()
            .sorted(
                (user1, user2) -> {
                  boolean user1Trained = admins.contains(user1.id());
                  boolean user2Trained = admins.contains(user2.id());

                  if (user1Trained && !user2Trained) {
                    return -1;
                  } else if (!user1Trained && user2Trained) {
                    return 1;
                  }

                  return user1.nick().compareToIgnoreCase(user2.nick());
                })
            .toList();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/admins");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/admins");
    }

    mv.addObject("users", users);
    mv.addObject("admins", admins);

    return mv;
  }

  @PutMapping("/admins")
  public ModelAndView setAdmins(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam Map<String, String> form) {
    List<UUID> oldAdmins = this.adminFacade.getAllAdmins();
    List<UUID> formAdmins =
        form.keySet().stream()
            .filter(s -> !("_csrf".equals(s) || "_method".equals(s)))
            .map(UUID::fromString)
            .toList();

    List<UUID> noLongerAdmins =
        oldAdmins.stream().filter(userId -> !formAdmins.contains(userId)).toList();

    try {
      this.adminFacade.updateAdmins(formAdmins, noLongerAdmins);
    } catch (IllegalArgumentException e) {
      return this.getAdmins(htmxRequest);
    }

    return new ModelAndView("redirect:/admins");
  }
}
