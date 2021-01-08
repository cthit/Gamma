package it.chalmers.gamma.group.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.FKITGroupDTO;

import it.chalmers.gamma.domain.membership.NoAccountMembershipDTO;
import it.chalmers.gamma.domain.membership.RestrictedMembershipDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFKITGroupResponse {
    @JsonUnwrapped
    private final FKITGroupDTO group;
    private final List<RestrictedMembershipDTO> groupMembers;
    private final List<NoAccountMembershipDTO> noAccountMembers;

    public GetFKITGroupResponse(FKITGroupDTO group,
                                List<RestrictedMembershipDTO> groupMembers,
                                List<NoAccountMembershipDTO> noAccountMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
        this.noAccountMembers = noAccountMembers;
    }

    public GetFKITGroupResponse(FKITGroupDTO group, List<RestrictedMembershipDTO> groupMembers) {
        this(group, groupMembers, null);
    }

    public FKITGroupDTO getGroup() {
        return this.group;
    }

    public List<RestrictedMembershipDTO> getGroupMembers() {
        return this.groupMembers;
    }

    public List<NoAccountMembershipDTO> getNoAccountMembers() {
        return this.noAccountMembers;
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
