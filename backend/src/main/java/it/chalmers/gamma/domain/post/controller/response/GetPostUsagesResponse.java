package it.chalmers.gamma.domain.post.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

import it.chalmers.gamma.domain.membership.data.dto.MembershipsPerGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostUsagesResponse {

    @JsonValue
    private final List<MembershipsPerGroupDTO> groups;

    public GetPostUsagesResponse(List<MembershipsPerGroupDTO> groups) {
        this.groups = groups;
    }

    public List<MembershipsPerGroupDTO> getGroups() {
        return groups;
    }

    public GetPostUsagesResponseObject toResponseObject() {
        return new GetPostUsagesResponseObject(this);
    }

    public static class GetPostUsagesResponseObject extends ResponseEntity<GetPostUsagesResponse> {
        GetPostUsagesResponseObject(GetPostUsagesResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
