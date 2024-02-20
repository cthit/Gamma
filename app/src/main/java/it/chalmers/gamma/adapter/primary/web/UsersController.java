package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsersController {

  private final UserFacade userFacade;
  private final UserCreationFacade userCreationFacade;

  public UsersController(UserFacade userFacade, UserCreationFacade userCreationFacade) {
    this.userFacade = userFacade;
    this.userCreationFacade = userCreationFacade;
  }

  @GetMapping("/users")
  public ModelAndView getUsers(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<UserFacade.UserDTO> users = this.userFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/users");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/users");
    }

    mv.addObject("users", users);

    return mv;
  }

  @GetMapping("/users/{id}")
  public ModelAndView getUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID userId) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/user-details");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/user-details");
    }

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthenticated) {
      if (userAuthenticated.isAdmin()) {
        Optional<UserFacade.UserExtendedWithGroupsDTO> userExtended =
            this.userFacade.getAsAdmin(userId);

        if (userExtended.isEmpty()) {
          throw new RuntimeException();
        }

        UserFacade.UserExtendedWithGroupsDTO u = userExtended.get();
        String name = u.user().firstName() + " '" + u.user().nick() + "' " + u.user().lastName();

        mv.addObject("userId", u.user().id());
        mv.addObject("name", name);
        mv.addObject("nick", u.user().nick());
        mv.addObject("cid", u.user().cid());
        mv.addObject("acceptanceYear", u.user().acceptanceYear());
        mv.addObject(
            "groups",
            u.groups().stream()
                .map(
                    userGroup -> userGroup.group().prettyName() + " - " + userGroup.post().enName())
                .toList());

        mv.addObject("email", u.user().email());
        mv.addObject("locked", u.user().locked());
      } else {
        Optional<UserFacade.UserWithGroupsDTO> user = this.userFacade.get(userId);

        if (user.isEmpty()) {
          throw new RuntimeException();
        }

        UserFacade.UserWithGroupsDTO u = user.get();
        String name = u.user().firstName() + " '" + u.user().nick() + "' " + u.user().lastName();

        mv.addObject("userId", u.user().id());
        mv.addObject("name", name);
        mv.addObject("nick", u.user().nick());
        mv.addObject("cid", u.user().cid());
        mv.addObject("acceptanceYear", u.user().acceptanceYear());
        mv.addObject(
            "groups",
            u.groups().stream()
                .map(
                    userGroup -> userGroup.group().prettyName() + " - " + userGroup.post().enName())
                .toList());
      }
    }

    return mv;
  }

  public record CreateUser(
      String password,
      String nick,
      String firstName,
      String lastName,
      String email,
      int acceptanceYear,
      String cid,
      String language) {}

  @GetMapping("/users/create")
  public ModelAndView getCreateUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/create-user");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/create-user");
    }

    mv.addObject("form", new CreateUser("", "", "", "", "", Year.now().getValue(), "", ""));

    return mv;
  }

  @PostMapping("/users")
  public ModelAndView createUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, CreateUser form) {
    try {
      UUID userId =
          this.userCreationFacade.createUser(
              new UserCreationFacade.NewUser(
                  form.password,
                  form.nick,
                  form.firstName,
                  form.lastName,
                  form.email,
                  form.acceptanceYear,
                  form.cid,
                  form.language));

      return new ModelAndView("redirect:/users/" + userId);
    } catch (UserCreationFacade.SomePropertyNotUniqueException e) {
      throw new RuntimeException(e);
    }
  }

  public record EditUser(
      String nick,
      String firstName,
      String lastName,
      String email,
      int acceptanceYear,
      String language) {}

  @GetMapping("/users/{id}/edit")
  public ModelAndView getEditUser(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId) {
    Optional<UserFacade.UserExtendedWithGroupsDTO> user = this.userFacade.getAsAdmin(userId);

    if (user.isEmpty()) {
      throw new RuntimeException();
    }

    UserFacade.UserExtendedDTO u = user.get().user();

    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/edit-user");
    mv.addObject("userId", u.id());
    mv.addObject(
        "form",
        new EditUser(
            u.nick(), u.firstName(), u.lastName(), u.email(), u.acceptanceYear(), u.language()));

    return mv;
  }

  @PutMapping("/users/{id}")
  public ModelAndView editUser(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId,
      EditUser form) {
    this.userFacade.updateUser(
        new UserFacade.UpdateUser(
            userId,
            form.nick,
            form.firstName,
            form.lastName,
            form.email,
            form.language,
            form.acceptanceYear));

    Optional<UserFacade.UserExtendedWithGroupsDTO> user = this.userFacade.getAsAdmin(userId);

    if (user.isEmpty()) {
      throw new RuntimeException();
    }

    String name = form.firstName() + " '" + form.nick() + "' " + form.lastName();

    ModelAndView mv = new ModelAndView();

    mv.setViewName("partial/edited-user");
    mv.addObject("name", name);
    mv.addObject("cid", user.get().user().cid());
    mv.addObject("acceptanceYear", form.acceptanceYear);
    mv.addObject("email", form.email);
    mv.addObject("locked", user.get().user().locked());
    mv.addObject("userId", userId);

    return mv;
  }
}
