package it.chalmers.gamma.group.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.group.dto.GroupDTO;

import it.chalmers.gamma.membership.dto.RestrictedMembershipDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFKITGroupResponse {

    @JsonUnwrapped
    private final GroupDTO group;
    private final List<RestrictedMembershipDTO> groupMembers;

    public GetFKITGroupResponse(GroupDTO group, List<RestrictedMembershipDTO> groupMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
    }

    public GroupDTO getGroup() {
        return this.group;
    }

    public List<RestrictedMembershipDTO> getGroupMembers() {
        return this.groupMembers;
    }

    public GetFKITGroupResponseObject toResponseObject() {
        return new GetFKITGroupResponseObject(this);
    }

    public static class GetFKITGroupResponseObject extends ResponseEntity<GetFKITGroupResponse> {
        public GetFKITGroupResponseObject(GetFKITGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
