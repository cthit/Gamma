package it.chalmers.gamma.filter;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.PasswordResetService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class ResetNonActivatedAccountFilter extends OncePerRequestFilter {

    private final String baseFrontendUrl;
    private static final String USERNAME_PARAMETER = "username";
    private final ITUserService itUserService;
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
            ITUserDTO userDTO = this.itUserService.getITUser(username);
            if (!userDTO.isActivated()) {
                this.passwordResetService.handlePasswordReset(userDTO);
                response.sendRedirect(String.format("%s/reset-password/finish", this.baseFrontendUrl));
                return;
            }
        }
        filterChain.doFilter(request, response);

    }
}
