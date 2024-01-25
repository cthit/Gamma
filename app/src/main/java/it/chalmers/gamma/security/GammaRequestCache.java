package it.chalmers.gamma.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

//@Component
public class GammaRequestCache implements RequestCache {

    private final RequestCache requestCache;

    public GammaRequestCache() {
        requestCache = new HttpSessionRequestCache();
    }

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if(request.getAuthType() == null) {
            requestCache.saveRequest(request, response);
        }
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        return requestCache.getRequest(request, response);
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        return requestCache.getMatchingRequest(request, response);
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        requestCache.removeRequest(request, response);
    }
}
