package it.chalmers.gamma.response.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITGroupToSuperGroupDTO;
import it.chalmers.gamma.domain.dto.user.ITUserDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetITUserResponse {

    @JsonUnwrapped
    private final ITUserDTO user;
    private final List<FKITGroupToSuperGroupDTO> groupRelationships;
    private final List<WebsiteUrlDTO> websiteURLs;

    public GetITUserResponse(ITUserDTO user,
                             List<FKITGroupToSuperGroupDTO> groupRelationships,
                             List<WebsiteUrlDTO> websiteURLs) {
        this.user = user;
        this.groupRelationships = groupRelationships;
        this.websiteURLs = websiteURLs;
    }

    public GetITUserResponse(ITUserDTO user) {
        this(user, null, null);
    }

    @JsonUnwrapped
    public ITUserDTO getUser() {
        return this.user;
    }

    public List<FKITGroupDTO> getGroups() {
        return this.groupRelationships.stream().map(FKITGroupToSuperGroupDTO::getGroup)
                .collect(Collectors.toList());
    }

    public List<WebsiteUrlDTO> getWebsiteURLs() {
        return this.websiteURLs;
    }

    public List<FKITGroupToSuperGroupDTO> getRelationships() {
        return this.groupRelationships;
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
