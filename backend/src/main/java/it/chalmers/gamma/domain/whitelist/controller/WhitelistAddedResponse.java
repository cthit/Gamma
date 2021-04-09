package it.chalmers.gamma.domain.whitelist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WhitelistAddedResponse extends ResponseEntity<String> {

    public WhitelistAddedResponse() {
        super(HttpStatus.OK);
    }
}
