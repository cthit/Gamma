package it.chalmers.gamma.supergroup.response;

import it.chalmers.gamma.group.dto.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetGroupsBySuperGroupResponse {

    private final List<GroupMinifiedDTO> groups;

    public GetGroupsBySuperGroupResponse(List<GroupMinifiedDTO> groups) {
        this.groups = groups;
    }

    public GetGroupsBySuperGroupResponseObject toResponseObject() {
        return new GetGroupsBySuperGroupResponseObject(this);
    }

    public static class GetGroupsBySuperGroupResponseObject extends ResponseEntity<GetGroupsBySuperGroupResponse>{

        public GetGroupsBySuperGroupResponseObject(GetGroupsBySuperGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
