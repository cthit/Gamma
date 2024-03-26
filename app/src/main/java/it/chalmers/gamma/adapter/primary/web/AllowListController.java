package it.chalmers.gamma.adapter.primary.web;

import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AllowListController {

  private final AllowListFacade allowListFacade;

  public AllowListController(AllowListFacade allowListFacade) {
    this.allowListFacade = allowListFacade;
  }

  @GetMapping("/allow-list")
  public ModelAndView getAllowList(
      @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
      @Nullable String cidInputError) {
    ModelAndView mv = new ModelAndView();
    if (htmxRequest) {
      mv.setViewName("pages/allow-list");
    } else {
      mv.setViewName("index");
      mv.addObject("page", "pages/allow-list");
    }

    List<String> allowList = this.allowListFacade.getAllowList();
    mv.addObject("allowList", allowList);

    if (cidInputError != null) {
      mv.addObject("cidInputError", cidInputError);
    }

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
      return getAllowList(htmxRequest, e.getMessage());
    }

    return new ModelAndView("redirect:allow-list");
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
