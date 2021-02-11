package it.chalmers.gamma.domain.group.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupDTO;

import it.chalmers.gamma.domain.membership.data.MembershipRestrictedDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetGroupResponse {

    @JsonUnwrapped
    private final GroupDTO group;
    private final List<MembershipRestrictedDTO> groupMembers;

    public GetGroupResponse(GroupDTO group, List<MembershipRestrictedDTO> groupMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
    }

    public GroupDTO getGroup() {
        return this.group;
    }

    public List<MembershipRestrictedDTO> getGroupMembers() {
        return groupMembers;
    }

    public GetGroupResponseObject toResponseObject() {
        return new GetGroupResponseObject(this);
    }

    public static class GetGroupResponseObject extends ResponseEntity<GetGroupResponse> {
        public GetGroupResponseObject(GetGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
