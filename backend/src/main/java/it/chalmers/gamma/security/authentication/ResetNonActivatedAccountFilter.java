package it.chalmers.gamma.security.authentication;

import it.chalmers.gamma.app.domain.Cid;
import it.chalmers.gamma.app.domain.User;
import it.chalmers.gamma.app.user.service.UserService;
import it.chalmers.gamma.app.service.UserLockedService;
import it.chalmers.gamma.app.userpasswordreset.service.PasswordResetService;
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
    private final PasswordResetService passwordResetService;
    private final UserService userService;
    private final UserLockedService userLockedService;

    public ResetNonActivatedAccountFilter(String baseFrontendUrl,
                                          PasswordResetService passwordResetService,
                                          UserService userService,
                                          UserLockedService userLockedService) {
        this.baseFrontendUrl = baseFrontendUrl;
        this.passwordResetService = passwordResetService;
        this.userService = userService;
        this.userLockedService = userLockedService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getParameter(USERNAME_PARAMETER);
        if (username != null) {
            try {
                User user = this.userService.get(Cid.valueOf(username));
                if (this.userLockedService.isLocked(user.id())) {
                    this.passwordResetService.handlePasswordReset(user);
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
