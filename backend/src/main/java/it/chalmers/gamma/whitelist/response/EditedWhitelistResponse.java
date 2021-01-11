package it.chalmers.gamma.whitelist.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EditedWhitelistResponse extends ResponseEntity<String> {
    public EditedWhitelistResponse() {
        super("WHITELIST_EDITED", HttpStatus.ACCEPTED);
    }
}