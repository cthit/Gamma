package it.chalmers.gamma.adapter.primary.thymeleaf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

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
