package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;

public class WebsiteNotFoundResponse extends CustomResponseStatusException {

    public WebsiteNotFoundResponse() {
        super(HttpStatus.NOT_FOUND, "WEBSITE_NOT_FOUND");
    }
}
