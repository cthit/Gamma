package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Website;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetAllWebsitesResponse extends ResponseEntity<List<Website>> {
    public GetAllWebsitesResponse(List<Website> websites) {
        super(websites, HttpStatus.ACCEPTED);
    }
}
