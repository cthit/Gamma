package it.chalmers.gamma.domain.post.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.controller.response.GetGroupResponse;
import java.util.List;

import it.chalmers.gamma.domain.group.data.GroupWithMembersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostUsagesResponse {

    @JsonValue
    private final List<GroupWithMembersDTO> groups;

    public GetPostUsagesResponse(List<GroupWithMembersDTO> groups) {
        this.groups = groups;
    }

    public List<GroupWithMembersDTO> getGroups() {
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
