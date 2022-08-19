package it.chalmers.gamma.security;

import it.chalmers.gamma.adapter.secondary.jpa.user.TrustedUserDetailsRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelName;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityLevelRepository;
import it.chalmers.gamma.app.authoritylevel.domain.AuthorityType;
import it.chalmers.gamma.app.user.domain.GammaUser;
import it.chalmers.gamma.app.user.domain.UserAuthority;
import it.chalmers.gamma.security.principal.UserAuthenticationDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class UpdateUserPrincipalFilter implements Filter {

    private final TrustedUserDetailsRepository userDetailsRepository;
    private final AuthorityLevelRepository authorityLevelRepository;

    public UpdateUserPrincipalFilter(TrustedUserDetailsRepository userDetailsRepository, AuthorityLevelRepository authorityLevelRepository) {
        this.userDetailsRepository = userDetailsRepository;
        this.authorityLevelRepository = authorityLevelRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            if (!usernamePasswordAuthenticationToken.isAuthenticated()) {
                return;
            }

            final GammaUser gammaUser = userDetailsRepository.getGammaUserByUser();

            UserAuthenticationDetails userPrincipal = new UserAuthenticationDetails() {
                @Override
                public GammaUser get() {
                    return gammaUser;
                }

                @Override
                public boolean isAdmin() {
                    return authorityLevelRepository.getByUser(gammaUser.id()).contains(new UserAuthority(new AuthorityLevelName("admin"), AuthorityType.AUTHORITY));
                }
            };


            usernamePasswordAuthenticationToken.setDetails(userPrincipal);
        }

        chain.doFilter(request, response);
    }
}
