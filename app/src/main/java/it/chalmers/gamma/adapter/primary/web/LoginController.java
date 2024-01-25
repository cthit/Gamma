package it.chalmers.gamma.adapter.primary.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    @GetMapping("/login")
    public ModelAndView getLogin(@RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "logout", required = false) String logout,
                                 @RequestParam(value = "authorizing", required = false) String authorizing,
                                 @RequestHeader(value = "HX-Request", required = false) boolean htmxRequest,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        ModelAndView mv = new ModelAndView();
        if(htmxRequest) {
            mv.setViewName("pages/login");
        } else {
            mv.setViewName("index");
            mv.addObject("page", "pages/login");
        }

        boolean isAuthorizing = authorizing != null;

        mv.addObject("error", error);
        mv.addObject("logout", logout);
        mv.addObject("authorizing", isAuthorizing);

        /*
         * There might be a situation where a user starts an authorizing
         * against a client, but stops while the redirect request has been cached.
         * This makes sure that the user when actually trying to login to the
         * Gamma frontend that they're redirected to that, and not redirected
         * to the consent page for example.
         */
        /*if (!isAuthorizing) {
            gammaRequestCache.removeRequest(request, response);
        }*/

        response.addHeader("HX-Retarget", "body");

        return mv;
    }

}