package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WebsiteNotFoundResponse extends ResponseStatusException {

    public WebsiteNotFoundResponse() {
        super(HttpStatus.NOT_FOUND, "WEBSITE_NOT_FOUND");
    }
}
