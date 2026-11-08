package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;
import static it.chalmers.gamma.app.common.UUIDValidator.isValidUUID;

import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.image.ImageFacade;
import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.UserFacade;
import it.chalmers.gamma.app.user.UserGdprTrainingFacade;
import it.chalmers.gamma.app.user.domain.AcceptanceYear.AcceptanceYearValidator;
import it.chalmers.gamma.app.user.domain.Cid.CidValidator;
import it.chalmers.gamma.app.user.domain.FirstName.FirstNameValidator;
import it.chalmers.gamma.app.user.domain.LastName.LastNameValidator;
import it.chalmers.gamma.app.user.domain.Nick.NickValidator;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword.UnencryptedPasswordValidator;
import it.chalmers.gamma.app.user.passwordreset.UserResetPasswordFacade;
import it.chalmers.gamma.security.authentication.AuthenticationExtractor;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Year;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsersController {

  private final UserFacade userFacade;
  private final UserCreationFacade userCreationFacade;
  private final UserResetPasswordFacade userResetPasswordFacade;
  private final UserGdprTrainingFacade userGdprTrainingFacade;
  private final ImageFacade imageFacade;

  public UsersController(
      UserFacade userFacade,
      UserCreationFacade userCreationFacade,
      UserResetPasswordFacade userResetPasswordFacade,
      UserGdprTrainingFacade userGdprTrainingFacade,
      ImageFacade imageFacade) {
    this.userFacade = userFacade;
    this.userCreationFacade = userCreationFacade;
    this.userResetPasswordFacade = userResetPasswordFacade;
    this.userGdprTrainingFacade = userGdprTrainingFacade;
    this.imageFacade = imageFacade;
  }

  @GetMapping("/users")
  public ModelAndView getUsers(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    List<UserFacade.UserDTO> users = this.userFacade.getAll();

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("users/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "users/page");
    }

    mv.addObject(
        "users",
        users.stream().sorted(Comparator.comparing(user -> user.nick().toLowerCase())).toList());

    return mv;
  }

  public record UserGroup(String text, UUID id) {}

  @GetMapping("/users/{id}")
  public ModelAndView getUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") String userId) {
    if (!isValidUUID(userId)) {
      return createUserNotFound(userId, htmxRequest);
    }

    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("user-details/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "user-details/page");
    }

    if (AuthenticationExtractor.getAuthentication()
        instanceof UserAuthentication userAuthenticated) {
      if (userAuthenticated.isAdmin()) {
        Optional<UserFacade.UserExtendedWithGroupsDTO> userExtended =
            this.userFacade.getAsAdmin(UUID.fromString(userId));

        if (userExtended.isEmpty()) {
          return createUserNotFound(userId, htmxRequest);
        }

        UserFacade.UserExtendedWithGroupsDTO u = userExtended.get();

        addAdminUserInfo(mv, u.user());
        mv.addObject("nick", u.user().nick());
        mv.addObject(
            "groups",
            u.groups().stream()
                .map(userGroup ->
                    new UserGroup(
                        userGroup.group().prettyName() + " - " + userGroup.post().enName(),
                        userGroup.group().id()))
                .toList());
      } else {
        Optional<UserFacade.UserWithGroupsDTO> user =
            this.userFacade.getWithGroups(UUID.fromString(userId));

        if (user.isEmpty()) {
          return createUserNotFound(userId, htmxRequest);
        }

        UserFacade.UserWithGroupsDTO u = user.get();
        String name = u.user().firstName() + " '" + u.user().nick() + "' " + u.user().lastName();

        mv.addObject("userId", u.user().id());
        mv.addObject("avatarVersion", this.imageFacade.getAvatarVersion(u.user().id()));
        mv.addObject("name", name);
        mv.addObject("nick", u.user().nick());
        mv.addObject("cid", u.user().cid());
        mv.addObject("acceptanceYear", u.user().acceptanceYear());
        mv.addObject(
            "groups",
            u.groups().stream()
                .map(userGroup ->
                    new UserGroup(
                        userGroup.group().prettyName() + " - " + userGroup.post().enName(),
                        userGroup.group().id()))
                .toList());
      }
    }

    return mv;
  }

  /** Everything that the user-details/page :: userinfo fragment needs for an admin. */
  private void addAdminUserInfo(ModelAndView mv, UserFacade.UserExtendedDTO user) {
    String name = user.firstName() + " '" + user.nick() + "' " + user.lastName();

    mv.addObject("userId", user.id());
    mv.addObject("avatarVersion", this.imageFacade.getAvatarVersion(user.id()));
    mv.addObject("name", name);
    mv.addObject("cid", user.cid());
    mv.addObject("acceptanceYear", user.acceptanceYear());
    mv.addObject("email", user.email());
    mv.addObject("locked", user.locked());
    mv.addObject("gdpr", this.userGdprTrainingFacade.hasGdprTraining(user.id()));
    mv.addObject("isSelf", isSignedInUser(user.id()));
  }

  private static boolean isSignedInUser(UUID userId) {
    return AuthenticationExtractor.getAuthentication() instanceof UserAuthentication signedIn
        && signedIn.gammaUser().id().value().equals(userId);
  }

  private ModelAndView createUserNotFound(String userId, boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("user-details/not-found");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "user-details/not-found");
    }

    mv.addObject("id", userId);

    return mv;
  }

  public record CreateUser(
      @ValidatedWith(UnencryptedPasswordValidator.class) String password,
      @ValidatedWith(NickValidator.class) String nick,
      @ValidatedWith(FirstNameValidator.class) String firstName,
      @ValidatedWith(LastNameValidator.class) String lastName,
      @ValidatedWith(EmailValidator.class) String email,
      @ValidatedWith(AcceptanceYearValidator.class) int acceptanceYear,
      @ValidatedWith(CidValidator.class) String cid,
      String language) {}

  @GetMapping("/users/create")
  public ModelAndView getCreateUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("create-user/page");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "create-user/page");
    }

    mv.addObject("form", new CreateUser("", "", "", "", "", Year.now().getValue(), "", ""));

    return mv;
  }

  @PostMapping("/users/create")
  public ModelAndView createUser(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateUser form,
      final BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    validateObject(form, bindingResult);

    try {
      if (!bindingResult.hasErrors()) {
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

        mv.setViewName("redirect:/users/" + userId);
      }
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new ObjectError("global", e.getMessage()));
    } catch (UserCreationFacade.CidNotUniqueException e) {
      bindingResult.addError(new FieldError("form", "cid", e.getMessage()));
    } catch (UserCreationFacade.EmailNotUniqueException e) {
      bindingResult.addError(new FieldError("form", "email", e.getMessage()));
    }

    if (bindingResult.hasErrors()) {
      if (htmxRequest) {
        mv.setViewName("create-user/page");
      } else {
        mv.setViewName("index");
        mv.addObject("page", "create-user/page");
      }

      mv.addObject("form", form);
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
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

    mv.setViewName("user-details/edit-user");
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
      EditUser form,
      BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    try {
      this.userFacade.updateUser(
          new UserFacade.UpdateUser(
              userId,
              form.nick,
              form.firstName,
              form.lastName,
              form.email,
              form.language,
              form.acceptanceYear));
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new ObjectError("global", e.getMessage()));

      mv.setViewName("user-details/edit-user");
      mv.addObject("userId", userId);
      mv.addObject("form", form);
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);

      return mv;
    }

    Optional<UserFacade.UserExtendedWithGroupsDTO> user = this.userFacade.getAsAdmin(userId);

    if (user.isEmpty()) {
      throw new RuntimeException();
    }

    mv.setViewName("user-details/edited-user");
    addAdminUserInfo(mv, user.get().user());

    return mv;
  }

  @PostMapping("/users/{id}/lock")
  public ModelAndView lockUser(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId,
      HttpServletResponse response) {
    return setLocked(userId, true, response);
  }

  @PostMapping("/users/{id}/unlock")
  public ModelAndView unlockUser(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId,
      HttpServletResponse response) {
    return setLocked(userId, false, response);
  }

  private ModelAndView setLocked(UUID userId, boolean locked, HttpServletResponse response) {
    try {
      this.userFacade.setLocked(userId, locked);
    } catch (UserFacade.CannotLockYourselfException e) {
      response.addHeader("HX-Retarget", "#alerts");
      response.addHeader("HX-Reswap", "afterbegin");
      return new ModelAndView("user-details/lock-yourself-error");
    }

    Optional<UserFacade.UserExtendedDTO> user = this.userFacade.getExtendedAsAdmin(userId);

    if (user.isEmpty()) {
      throw new RuntimeException();
    }

    ModelAndView mv = new ModelAndView("user-details/lock-changed");
    addAdminUserInfo(mv, user.get());

    return mv;
  }

  @DeleteMapping("/users/{id}")
  public ModelAndView deleteUser(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId) {

    this.userFacade.deleteUser(userId);

    return new ModelAndView("redirect:/users");
  }

  @PostMapping("/users/{id}/generate-password-link")
  public ModelAndView generatePasswordLink(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("id") UUID userId) {
    String passwordResetLink = userResetPasswordFacade.generatePasswordLink(userId);

    ModelAndView mv = new ModelAndView();
    mv.setViewName("user-details/page :: password-link-generated");
    mv.addObject("passwordLink", passwordResetLink);

    return mv;
  }
}
