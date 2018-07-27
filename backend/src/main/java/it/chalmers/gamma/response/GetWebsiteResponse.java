package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Website;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetWebsiteResponse extends ResponseEntity<Website> {
    public GetWebsiteResponse(Website website) {
        super(website, HttpStatus.ACCEPTED);
    }
}
