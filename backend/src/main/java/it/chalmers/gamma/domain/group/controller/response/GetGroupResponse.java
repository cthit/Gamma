package it.chalmers.gamma.domain.group.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.dto.GroupDTO;

import it.chalmers.gamma.domain.membership.data.dto.MembershipRestrictedDTO;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetGroupResponse {

    @JsonUnwrapped
    public final GroupDTO group;
    public final List<MembershipRestrictedDTO> groupMembers;

    public GetGroupResponse(GroupDTO group, List<MembershipRestrictedDTO> groupMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
    }

}
