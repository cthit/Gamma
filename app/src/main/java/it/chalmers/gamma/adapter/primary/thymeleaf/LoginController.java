package it.chalmers.gamma.adapter.primary.thymeleaf;

import it.chalmers.gamma.security.GammaRequestCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final GammaRequestCache gammaRequestCache;

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;

    public LoginController(GammaRequestCache gammaRequestCache) {
        this.gammaRequestCache = gammaRequestCache;
    }

    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "authorizing", required = false) String authorizing,
                           Model model,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        boolean isAuthorizing = authorizing != null;

        model.addAttribute("createAccountUrl", this.baseFrontendUrl + "/create-account");
        model.addAttribute("forgotPasswordUrl", this.baseFrontendUrl + "/reset-password");
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);
        model.addAttribute("authorizing", isAuthorizing);

        /*
         * There might be a situation where a user starts an authorizing
         * against a client, but stops while the redirect request has been cached.
         * This makes sure that the user when actually trying to login to the
         * Gamma frontend that they're redirect to that, and not redirected
         * to the consent page for example.
         */
        if (!isAuthorizing) {
            gammaRequestCache.removeRequest(request, response);
        }

        return "old/login";
    }

}