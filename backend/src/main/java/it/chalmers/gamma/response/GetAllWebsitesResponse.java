package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.Website;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWebsitesResponse extends ResponseEntity<List<Website>> {
    public GetAllWebsitesResponse(List<Website> websites) {
        super(websites, HttpStatus.ACCEPTED);
    }
}
