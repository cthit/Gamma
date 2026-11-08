package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.MeFacade;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DeleteYourAccountController {

  private final MeFacade meFacade;

  public DeleteYourAccountController(MeFacade meFacade) {
    this.meFacade = meFacade;
  }

  public record DeleteYourAccountForm(String password) {}

  public record ClientOwnerContact(
      String name, String email, List<String> clients, String mailto) {}

  @GetMapping("/delete-your-account")
  public ModelAndView getDeleteYourAccount(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();

    if (htmxRequest) {
      mv.setViewName("pages/delete-your-account");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/delete-your-account");
    }

    mv.addObject("form", new DeleteYourAccountForm(""));
    addClients(mv);

    return mv;
  }

  @DeleteMapping("/delete-your-account")
  public ModelAndView deleteYourAccount(
      DeleteYourAccountForm form, final BindingResult bindingResult) {
    try {
      this.meFacade.deleteMe(form.password);
    } catch (IllegalArgumentException e) {
      bindingResult.addError(new FieldError("form", "password", "Incorrect password"));

      ModelAndView mv = new ModelAndView();

      mv.setViewName("pages/delete-your-account");
      mv.addObject("form", new DeleteYourAccountForm(""));
      mv.addObject(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
      addClients(mv);

      return mv;
    }

    return new ModelAndView("redirect:/login?deleted");
  }

  private void addClients(ModelAndView mv) {
    MeFacade.AccountDeletionOverviewDTO overview = this.meFacade.getAccountDeletionOverview();

    mv.addObject("officialClients", overview.officialClients());
    mv.addObject(
        "clientOwners",
        overview.unofficialClientOwners().stream()
            .map(
                owner ->
                    new ClientOwnerContact(
                        owner.name(),
                        owner.email(),
                        owner.clients(),
                        MailtoLink.href(
                            owner.email(), subject(owner.clients()), body(overview, owner))))
            .toList());
  }

  private static String subject(List<String> clients) {
    return "Delete my data from " + String.join(", ", clients);
  }

  private static String body(
      MeFacade.AccountDeletionOverviewDTO me, MeFacade.UnofficialClientOwnerDTO owner) {
    String clients = String.join(", ", owner.clients());
    return """
        %s

        I am deleting my Gamma account. Please delete all data you have about me in %s.

        My Gamma user id is %s and my cid is %s.

        Thanks,
        %s
        """
        .formatted(
            owner.firstName() == null ? "Hi," : "Hi " + owner.firstName() + ",",
            clients,
            me.userId(),
            me.cid(),
            me.firstName());
  }
}
