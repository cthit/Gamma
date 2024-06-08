package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.client.domain.ClientUid;
import it.chalmers.gamma.app.migration.ClientApproving;
import it.chalmers.gamma.app.user.domain.UserId;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is only used as a part of migration from old gamma to new gamma. We want to transfer the
 * accepting of a client for certain project, such as HubbIT and BookIT.
 */
@Controller
public class ClientApprovingController {

  private final ClientApproving clientApproving;

  public ClientApprovingController(ClientApproving clientApproving) {
    this.clientApproving = clientApproving;
  }

  public record ClientApprovingForm(String clientUid, String userIds) {}

  @GetMapping("/client-approving")
  public ModelAndView getClientApproving(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/client-approving");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/client-approving");
    }

    mv.addObject("form", new ClientApprovingForm("", ""));

    return mv;
  }

  @PostMapping("/client-approving")
  public ModelAndView massApproveClient(ClientApprovingForm form) {
    List<UserId> userIds = Arrays.stream(form.userIds.split(",")).map(UserId::valueOf).toList();

    clientApproving.approve(ClientUid.valueOf(form.clientUid), userIds);

    return new ModelAndView("redirect:/clients/" + form.clientUid);
  }
}
