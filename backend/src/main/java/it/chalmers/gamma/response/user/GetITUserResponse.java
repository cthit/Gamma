package it.chalmers.gamma.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final ITUserDTO user;
    private final List<FKITGroupDTO> groups;
    private final List<WebsiteUrlDTO> websiteURLs;

    public GetITUserResponse(ITUserDTO user,
                             List<FKITGroupDTO> groups,
                             List<WebsiteUrlDTO> websiteURLs) {
        this.user = user;
        this.groups = groups;
        this.websiteURLs = websiteURLs;
    }

    public GetITUserResponse(ITUserDTO user) {
        this(user, null, null);
    }

    @JsonUnwrapped
    public ITUserDTO getUser() {
        return this.user;
    }

    @JsonIgnore
    public List<FKITGroupDTO> getGroups() {
        return this.groups;
    }

    public List<WebsiteUrlDTO> getWebsiteURLs() {
        return this.websiteURLs;
    }

    @JsonIgnore
    public GetITUserResponseObject toResponseObject() {
        return new GetITUserResponseObject(this);
    }

    public static class GetITUserResponseObject extends ResponseEntity<GetITUserResponse> {
        GetITUserResponseObject(GetITUserResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
