package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.Website;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetWebsiteResponse extends ResponseEntity<Website> {
    public GetWebsiteResponse(Website website) {
        super(website, HttpStatus.ACCEPTED);
    }
}
