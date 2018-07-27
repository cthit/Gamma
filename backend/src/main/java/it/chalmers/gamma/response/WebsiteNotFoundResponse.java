package it.chalmers.gamma.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebsiteNotFoundResponse extends ResponseEntity<String> {

    public WebsiteNotFoundResponse() {
        super("WEBSITE_NOT_FOUND", HttpStatus.ACCEPTED);
    }
}
