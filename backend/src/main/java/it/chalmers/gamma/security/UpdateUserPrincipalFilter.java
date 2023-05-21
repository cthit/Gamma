package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.app.authority.domain.ClientAuthorityRepository;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.security.authentication.UserAuthentication;
import jakarta.servlet.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateUserPrincipalFilter implements Filter {

    private final TrustedUserDetailsRepository userDetailsRepository;
    private final ClientAuthorityRepository clientAuthorityRepository;

    public UpdateUserPrincipalFilter(TrustedUserDetailsRepository userDetailsRepository, ClientAuthorityRepository clientAuthorityRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.clientAuthorityRepository = clientAuthorityRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            if (!usernamePasswordAuthenticationToken.isAuthenticated()) {
                return;
            }

            final GammaUser gammaUser = userDetailsRepository.getGammaUserByUser();
            //TODO:
            final List<UserAuthority> authorities = new ArrayList<>();//this.clientAuthorityRepository.getByUser(gammaUser.id());

            UserAuthentication userPrincipal = new UserAuthentication() {
                @Override
                public GammaUser get() {
                    return gammaUser;
                }

                @Override
                public List<UserAuthority> getAuthorities() {
                    return authorities;
                }
            };


            usernamePasswordAuthenticationToken.setDetails(userPrincipal);
        }

        chain.doFilter(request, response);
    }
}
