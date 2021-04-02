package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.domain.group.data.dto.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetGroupsBySuperGroupResponse {

    public GetGroupsBySuperGroupResponse(List<GroupMinifiedDTO> groups) {
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
