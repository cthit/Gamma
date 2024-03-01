package it.chalmers.gamma.response.website;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedWebsiteResponse extends ResponseEntity<String> {
    public EditedWebsiteResponse() {
        super("EDITED_WEBSITE", HttpStatus.ACCEPTED);
    }
}
