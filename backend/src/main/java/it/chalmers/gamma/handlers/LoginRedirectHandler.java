package it.chalmers.gamma.handlers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginRedirectHandler implements AuthenticationSuccessHandler {


    @Value("${application.frontend-client-details.successful-login-uri}")
    private String frontendUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRedirectHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (response.isCommitted()) {
            LOGGER.warn("Response already committed, cannot redirect ");
        }
        response.sendRedirect(redirectUrl(request));
    }
    private String redirectUrl(HttpServletRequest request) {
        String setRedirect = (String) request.getSession().getAttribute("redirect");
        return setRedirect == null ? frontendUrl : setRedirect;
    }
}
