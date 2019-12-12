package it.chalmers.gamma.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order()
public class JSONSentObjectFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONSentObjectFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ContentCachingResponseWrapper resWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, resWrapper);
        String payload = new String(resWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        LOGGER.info("Response from server was: {} {}", payload, resWrapper.getContentType());
        resWrapper.copyBodyToResponse();
    }

}
