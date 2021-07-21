package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.UserFacade;
import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.user.UserPasswordResetService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class ResetNonActivatedAccountFilter extends OncePerRequestFilter {

    private final String baseFrontendUrl;
    private static final String USERNAME_PARAMETER = "username";
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetNonActivatedAccountFilter.class);
    private final UserPasswordResetService userPasswordResetService;
    private final UserFacade userFacade;

    public ResetNonActivatedAccountFilter(String baseFrontendUrl,
                                          UserPasswordResetService userPasswordResetService,
                                          UserFacade userFacade) {
        this.baseFrontendUrl = baseFrontendUrl;
        this.userPasswordResetService = userPasswordResetService;
        this.userFacade = userFacade;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getParameter(USERNAME_PARAMETER);
        if (username != null) {
            try {
                User user = this.userFacade.get(Cid.valueOf(username));
                if (user.locked()) {
                    this.userPasswordResetService.handlePasswordReset(user);
                    String params = "accountLocked=true";
                    response.sendRedirect(String.format("%s/reset-password/finish?%s", this.baseFrontendUrl, params));
                    return;
                }
            } catch (UserService.UserNotFoundException e) {
                LOGGER.info(String.format("User %s tried logging in, but no such user exists", username));
            }
        }
        filterChain.doFilter(request, response);
    }
}
