package it.chalmers.gamma.filter;

import it.chalmers.gamma.domain.user.ITUserDTO;
import it.chalmers.gamma.user.response.UserNotFoundResponse;
import it.chalmers.gamma.user.ITUserService;
import it.chalmers.gamma.passwordreset.PasswordResetService;
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
    private final ITUserService itUserService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResetNonActivatedAccountFilter.class);
    private final PasswordResetService passwordResetService;

    public ResetNonActivatedAccountFilter(ITUserService itUserService,
                                          PasswordResetService passwordResetService,
                                          String baseFrontendUrl) {
        this.itUserService = itUserService;
        this.passwordResetService = passwordResetService;
        this.baseFrontendUrl = baseFrontendUrl;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getParameter(USERNAME_PARAMETER);
        if (username != null) {
            try {
                ITUserDTO userDTO = this.itUserService.getITUser(username);
                if (!userDTO.isActivated()) {
                    this.passwordResetService.handlePasswordReset(userDTO);
                    String params = "accountLocked=true";
                    response.sendRedirect(String.format("%s/reset-password/finish?%s", this.baseFrontendUrl, params));
                    return;
                }
            } catch (UserNotFoundResponse e) {
                LOGGER.info(String.format("User %s tried logging in, but no such user exists", username));
            }
        }
        filterChain.doFilter(request, response);

    }
}
