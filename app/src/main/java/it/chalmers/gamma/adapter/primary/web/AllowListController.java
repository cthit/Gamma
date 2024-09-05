package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.activation.ActivationCodeFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AllowListController {

  private final AllowListFacade allowListFacade;
  private final ActivationCodeFacade activationCodeFacade;

  public AllowListController(
      AllowListFacade allowListFacade, ActivationCodeFacade activationCodeFacade) {
    this.allowListFacade = allowListFacade;
    this.activationCodeFacade = activationCodeFacade;
  }

  public record AllowListItem(String cid, boolean hasActivationCode) {}

  @GetMapping("/allow-list")
  public ModelAndView getAllowList(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/allow-list");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/allow-list");
    }

    List<String> allowList = this.allowListFacade.getAllowList();
    List<String> activationList =
        this.activationCodeFacade.getAllUserActivations().stream()
            .map(ActivationCodeFacade.UserActivationDTO::cid)
            .toList();

    mv.addObject(
        "allowList",
        allowList.stream()
            .sorted(Comparator.comparing(String::toLowerCase))
            .map(cid -> new AllowListItem(cid, activationList.contains(cid)))
            .toList());

    return mv;
  }

  @PutMapping(value = "/allow-list")
  public ModelAndView allow(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      AllowCidForm request) {
    try {
      allowListFacade.allow(request.cid);
    } catch (AllowListRepository.AlreadyAllowedException
        | IllegalArgumentException
        | AllowListFacade.AlreadyAUserException e) {
      return getAllowList(htmxRequest);
    }

    return new ModelAndView("redirect:/allow-list");
  }

  public record AllowCidForm(String cid) {}

  @DeleteMapping(value = "/allow-list/{cid}")
  public ModelAndView removeCidFromAllowList(
      @RequestHeader(value = "HX-Request", required = true) boolean htmxRequest,
      @PathVariable("cid") String cid) {

    this.allowListFacade.removeFromAllowList(cid);

    return new ModelAndView("common/empty");
  }
}
