package it.chalmers.gamma.response.website;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWebsitesResponse {
    private final List<GetWebsiteResponse> responses;

    public GetAllWebsitesResponse(List<GetWebsiteResponse> responses) {
        this.responses = responses;
    }

    public List<GetWebsiteResponse> getResponses() {
        return responses;
    }

    public GetAllWebsitesResponseObject getResponseObject() {
        return new GetAllWebsitesResponseObject(this);
    }

    public static class GetAllWebsitesResponseObject extends ResponseEntity<GetAllWebsitesResponse> {
        GetAllWebsitesResponseObject(GetAllWebsitesResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
