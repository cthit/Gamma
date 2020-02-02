package it.chalmers.gamma.filter;

import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.service.ITUserService;
import it.chalmers.gamma.service.PasswordResetService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

public class ResetNonActivatedAccountFilter extends OncePerRequestFilter {

    private final String baseFrontendUrl;
    private final String usernameParameter = "username";
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
        String username = request.getParameter(usernameParameter);
        if(username != null) {
            ITUserDTO userDTO = itUserService.getITUser(username);
            if(!userDTO.isActivated()) {
                passwordResetService.handlePasswordReset(userDTO);
                response.sendRedirect(String.format("%s/reset-password/finish", baseFrontendUrl));
                return;
            }
        }
        filterChain.doFilter(request, response);

    }
}
