package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.app.admin.domain.AdminRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class UpdateUserPrincipalFilter implements Filter {

    private final TrustedUserDetailsRepository userDetailsRepository;
    private final AdminRepository adminRepository;

    public UpdateUserPrincipalFilter(TrustedUserDetailsRepository userDetailsRepository, AdminRepository adminRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            if (!usernamePasswordAuthenticationToken.isAuthenticated()) {
                return;
            }

            final GammaUser gammaUser = userDetailsRepository.getGammaUserByUser();
            final boolean isAdmin = adminRepository.isAdmin(gammaUser.id());

            UserAuthentication userPrincipal = new UserAuthentication() {
                @Override
                public GammaUser get() {
                    return gammaUser;
                }

                @Override
                public boolean isAdmin() {
                    return isAdmin;
                }
            };


            usernamePasswordAuthenticationToken.setDetails(userPrincipal);
        }

        chain.doFilter(request, response);
    }
}
