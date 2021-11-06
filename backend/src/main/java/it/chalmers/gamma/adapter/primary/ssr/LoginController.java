package it.chalmers.gamma.adapter.primary.ssr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Value("${application.frontend-client-details.successful-login-uri}")
    private String baseFrontendUrl;

    @GetMapping("/login")
    public String getLogin(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        model.addAttribute("createAccountUrl", this.baseFrontendUrl + "/create-account");
        model.addAttribute("forgotPasswordUrl", this.baseFrontendUrl + "/reset-password");
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);

        return "login";
    }

}