package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.user.MeFacade;
import it.chalmers.gamma.app.user.UserResetPasswordFacade;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

public class ResetNonActivatedAccountFilter extends OncePerRequestFilter {

    private static final String USERNAME_PARAMETER = "username";
    private final UserResetPasswordFacade userResetPasswordFacade;
    private final MeFacade meFacade;
    private final String baseFrontendUrl;

    public ResetNonActivatedAccountFilter(UserResetPasswordFacade userResetPasswordFacade, MeFacade meFacade, @Value("${application.frontend-client-details.successful-login-uri}")
            String baseFrontendUrl) {
        this.userResetPasswordFacade = userResetPasswordFacade;
        this.meFacade = meFacade;
        this.baseFrontendUrl = baseFrontendUrl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//        String username = request.getParameter(USERNAME_PARAMETER);
//        if (username != null) {
//            User user = this.userFacade.get(Cid.valueOf(username));
//            if (user.locked()) {
//                this.userPasswordResetService.handlePasswordReset(user);
//                String params = "accountLocked=true";
//                response.sendRedirect(String.format("%s/reset-password/finish?%s", this.baseFrontendUrl, params));
//                return;
//            }
//        }
//        filterChain.doFilter(request, response);
    }
}
