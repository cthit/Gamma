package it.chalmers.gamma.response.website;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WebsiteDeletedResponse extends ResponseEntity<String> {
    public WebsiteDeletedResponse() {
        super("DELETED_WEBSITE", HttpStatus.ACCEPTED);
    }
}
