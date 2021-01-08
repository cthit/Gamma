package it.chalmers.gamma.oauth;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class OAuthRedirectFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestURI = httpServletRequest.getRequestURI();
        SecurityContext context = SecurityContextHolder.getContext();
        String prefix = ((HttpServletRequest) request).getContextPath();
        if (requestURI.startsWith(prefix + "/oauth")
                && (context.getAuthentication() == null || !context.getAuthentication().isAuthenticated())) {
            ((HttpServletRequest) request).getSession().setAttribute("redirect", requestURI
                    + "?" + httpServletRequest.getQueryString());
            httpServletResponse.sendRedirect(String.format("%s/login", httpServletRequest.getContextPath()));
        } else {
            chain.doFilter(request, response);
        }
    }
}
