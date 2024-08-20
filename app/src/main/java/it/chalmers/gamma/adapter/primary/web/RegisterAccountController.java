package it.chalmers.gamma.adapter.primary.web;

import static it.chalmers.gamma.adapter.primary.web.WebValidationHelper.validateObject;

import it.chalmers.gamma.app.common.Email.EmailValidator;
import it.chalmers.gamma.app.user.UserCreationFacade;
import it.chalmers.gamma.app.user.activation.domain.UserActivationRepository;
import it.chalmers.gamma.app.user.domain.AcceptanceYear.AcceptanceYearValidator;
import it.chalmers.gamma.app.user.domain.Cid.CidValidator;
import it.chalmers.gamma.app.user.domain.FirstName.FirstNameValidator;
import it.chalmers.gamma.app.user.domain.LastName.LastNameValidator;
import it.chalmers.gamma.app.user.domain.Nick.NickValidator;
import it.chalmers.gamma.app.user.domain.UnencryptedPassword.UnencryptedPasswordValidator;
import java.time.Year;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterAccountController {

  private final UserCreationFacade userCreationFacade;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(RegisterAccountController.class.getName());

  public RegisterAccountController(UserCreationFacade userCreationFacade) {
    this.userCreationFacade = userCreationFacade;
  }

  public ModelAndView createGetActivateCid(
      boolean htmxRequest, ActivateCidForm form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("register-account/activate-cid");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "register-account/activate-cid");
    }

    if (form == null) {
      form = new ActivateCidForm("");
    }
    mv.addObject("form", form);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/activate-cid")
  public ModelAndView getActivateCid(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return createGetActivateCid(htmxRequest, null, null);
  }

  @PostMapping("/activate-cid")
  public ModelAndView activateCid(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      ActivateCidForm form,
      BindingResult bindingResult) {

    validateObject(form, bindingResult);

    if (bindingResult.hasErrors()) {
      return createGetActivateCid(htmxRequest, form, bindingResult);
    } else {
      this.userCreationFacade.tryToActivateUser(form.cid);
      return new ModelAndView("redirect:/email-sent");
    }
  }

  public record ActivateCidForm(@ValidatedWith(CidValidator.class) String cid) {}

  @GetMapping("/email-sent")
  public ModelAndView getEmailSent(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    mv.setViewName("register-account/email-sent");

    return mv;
  }

  public ModelAndView createGetRegister(
      boolean htmxRequest, CreateAccountForm form, BindingResult bindingResult) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("register-account/register-account");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "register-account/register-account");
    }

    mv.addObject("form", form);

    if (bindingResult != null && bindingResult.hasErrors()) {
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
    }

    return mv;
  }

  @GetMapping("/register")
  public ModelAndView getRegister(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @RequestParam(value = "token", required = true) String token) {
    if (!this.userCreationFacade.isValidToken(token)) {
      ModelAndView mv = new ModelAndView();
      if (htmxRequest) {
        mv.setViewName("pages/register-account-token-bad");
      } else {
        mv.setViewName("index");
        mv.addObject("page", "pages/register-account-token-bad");
      }

      return mv;
    }

    CreateAccountForm form =
        new CreateAccountForm(token, "", "", "", "", "", "", Year.now().getValue(), "SV", false);

    return createGetRegister(htmxRequest, form, null);
  }

  @PostMapping("/register")
  public ModelAndView registerAccount(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      CreateAccountForm form,
      BindingResult bindingResult) {

    validateObject(form, bindingResult);

    try {
      if (!bindingResult.hasErrors()) {
        this.userCreationFacade.createUserWithCode(
            new UserCreationFacade.NewUserByCode(
                form.password,
                form.nick,
                form.firstName,
                form.lastName,
                form.email,
                form.acceptanceYear,
                form.language),
            form.code,
            form.confirmPassword,
            form.acceptUserAgreement);
      }
    } catch (UserCreationFacade.SomePropertyNotUniqueRuntimeException e) {
      bindingResult.addError(
          new ObjectError(
              "global",
              "Please double check what you have entered. Please send an email to ita@chalmers.it if your issues persist."));
      LOGGER.info(
          "Some property wasn't unique when a user tried to create an account. More info on debug level...");
      LOGGER.debug(e.getMessage());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (UserActivationRepository.TokenNotActivatedRuntimeException e) {
      bindingResult.addError(
          new ObjectError(
              "global", "Token not valid anymore. Please request a new registration url."));
    }

    if (bindingResult.hasErrors()) {
      return createGetRegister(htmxRequest, form, bindingResult);
    } else {
      return new ModelAndView("redirect:/login?account-created");
    }
  }

  public record CreateAccountForm(
      String code,
      @ValidatedWith(UnencryptedPasswordValidator.class) String password,
      @ValidatedWith(UnencryptedPasswordValidator.class) String confirmPassword,
      @ValidatedWith(NickValidator.class) String nick,
      @ValidatedWith(FirstNameValidator.class) String firstName,
      @ValidatedWith(LastNameValidator.class) String lastName,
      @ValidatedWith(EmailValidator.class) String email,
      @ValidatedWith(AcceptanceYearValidator.class) int acceptanceYear,
      String language,
      boolean acceptUserAgreement) {}
}
