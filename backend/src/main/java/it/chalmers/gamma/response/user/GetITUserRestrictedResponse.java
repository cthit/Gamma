package it.chalmers.gamma.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserRestrictedDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserRestrictedResponse {

    @JsonUnwrapped
    private final ITUserRestrictedDTO user;
    private final List<FKITGroupDTO> groups;
    private final List<WebsiteUrlDTO> websiteURLs;

    public GetITUserRestrictedResponse(
            ITUserRestrictedDTO user,
            List<FKITGroupDTO> groups,
            List<WebsiteUrlDTO> websiteURLs) {
        this.user = user;
        this.groups = groups;
        this.websiteURLs = websiteURLs;
    }

    public ITUserRestrictedDTO getUser() {
        return this.user;
    }

    public List<FKITGroupDTO> getGroups() {
        return this.groups;
    }

    public List<WebsiteUrlDTO> getWebsiteURLs() {
        return this.websiteURLs;
    }

    @JsonIgnore
    public GetITUserRestrictedResponseObject toResponseObject() {
        return new GetITUserRestrictedResponseObject(this);
    }

    public static class GetITUserRestrictedResponseObject extends ResponseEntity<GetITUserRestrictedResponse> {
        GetITUserRestrictedResponseObject(GetITUserRestrictedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
