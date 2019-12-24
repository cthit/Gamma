package it.chalmers.gamma.response.website;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllWebsitesResponse {
    private final List<GetWebsiteResponse> websites;

    public GetAllWebsitesResponse(List<GetWebsiteResponse> websites) {
        this.websites = websites;
    }

    @JsonValue
    public List<GetWebsiteResponse> getWebsites() {
        return this.websites;
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
