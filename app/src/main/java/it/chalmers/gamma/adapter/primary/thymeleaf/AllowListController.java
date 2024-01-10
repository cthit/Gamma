package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.app.user.allowlist.AllowListFacade;
import it.chalmers.gamma.app.user.allowlist.AllowListRepository;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class AllowListController {

    private final AllowListFacade allowListFacade;

    public AllowListController(AllowListFacade allowListFacade) {
        this.allowListFacade = allowListFacade;
    }

    @GetMapping("/allow-list")
    public ModelAndView getAllowList(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, @Nullable String cidInputError) {
        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/allow-list");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/allow-list");
        }

        List<String> allowList = this.allowListFacade.getAllowList();
        mv.addObject("allowList", allowList);

        if(cidInputError != null) {
            mv.addObject("cidInputError", cidInputError);
        }

        return mv;
    }

    @PutMapping(value = "/allow-list")
    public ModelAndView allow(@RequestHeader(value = "HX-Request", required = false) boolean htmxRequest, AllowCidRequestBody request) {
        try {
            allowListFacade.allow(request.cid);
        } catch (AllowListRepository.AlreadyAllowedException | IllegalArgumentException e) {
            return getAllowList(htmxRequest, e.getMessage());
        }

        return new ModelAndView("redirect:allow-list");
    }

    public record AllowCidRequestBody (String cid) {}

}
