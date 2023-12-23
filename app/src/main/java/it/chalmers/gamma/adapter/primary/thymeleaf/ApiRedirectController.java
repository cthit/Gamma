package it.chalmers.gamma.adapter.primary.thymeleaf;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class ApiRedirectController {

    private final String frontendUrl;

    public ApiRedirectController(@Value("${application.frontend-client-details.successful-login-uri}") String frontendUrl) {
        this.frontendUrl = frontendUrl;
    }

    @GetMapping()
    public void redirectToFrontend(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Location", frontendUrl);
        httpServletResponse.setStatus(301);
    }
}
