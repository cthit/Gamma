package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.datadeletion.DataDeletionFacade;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRecipientRepository;
import it.chalmers.gamma.app.datadeletion.domain.DataDeletionRequestRepository;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DataDeletionController {

  private final DataDeletionFacade dataDeletionFacade;

  public DataDeletionController(DataDeletionFacade dataDeletionFacade) {
    this.dataDeletionFacade = dataDeletionFacade;
  }

  public record RecipientForm(String email) {}

  @GetMapping("/data-deletion")
  public ModelAndView getDataDeletion(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    return page(htmxRequest, "", null);
  }

  @PostMapping("/data-deletion/requests/{id}/clear")
  public ModelAndView clearRequest(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @PathVariable("id") UUID id) {
    try {
      this.dataDeletionFacade.clear(id);
    } catch (DataDeletionRequestRepository.RequestNotFoundException
        | DataDeletionRequestRepository.AlreadyClearedException e) {
    }

    return new ModelAndView("redirect:/data-deletion");
  }

  @PutMapping("/data-deletion/recipients")
  public ModelAndView addRecipient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      RecipientForm form) {
    String email = form.email() == null ? "" : form.email();
    try {
      this.dataDeletionFacade.addRecipient(email);
    } catch (IllegalArgumentException e) {
      return page(htmxRequest, email, "Not a valid email");
    } catch (DataDeletionRecipientRepository.RecipientAlreadyExistsException e) {
      return page(htmxRequest, email, "Already gets the email");
    }

    return new ModelAndView("redirect:/data-deletion");
  }

  @DeleteMapping("/data-deletion/recipients")
  public ModelAndView removeRecipient(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      RecipientForm form) {
    try {
      this.dataDeletionFacade.removeRecipient(form.email());
    } catch (DataDeletionRecipientRepository.RecipientNotFoundException
        | IllegalArgumentException e) {
    }

    return new ModelAndView("redirect:/data-deletion");
  }

  private ModelAndView page(boolean htmxRequest, String email, String emailError) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/data-deletion");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/data-deletion");
    }

    mv.addObject("openRequests", this.dataDeletionFacade.getOpenRequests());
    mv.addObject("clearedRequests", this.dataDeletionFacade.getClearedRequests());
    mv.addObject("recipients", this.dataDeletionFacade.getRecipients());
    mv.addObject("email", email);
    mv.addObject("emailError", emailError);

    return mv;
  }
}
