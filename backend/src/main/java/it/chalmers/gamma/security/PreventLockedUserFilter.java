package it.chalmers.gamma.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

//TODO: Or should I just in AuthenticatedService return Unauthenticated if locked?
@Component
public class PreventLockedUserFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreventLockedUserFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

}
