package it.chalmers.gamma.response.website;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWebsitesResponse {
    private final List<GetWebsiteResponse> websites;

    public GetAllWebsitesResponse(List<GetWebsiteResponse> websites) {
        this.websites = websites;
    }

    public List<GetWebsiteResponse> getWebsites() {
        return websites;
    }

    public GetAllWebsitesResponseObject toResponseObject() {
        return new GetAllWebsitesResponseObject(this);
    }

    public static class GetAllWebsitesResponseObject extends ResponseEntity<GetAllWebsitesResponse> {
        GetAllWebsitesResponseObject(GetAllWebsitesResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
