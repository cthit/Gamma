package it.chalmers.gamma.domain.group.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.service.GroupDTO;

import it.chalmers.gamma.domain.membership.service.MembershipRestrictedDTO;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetGroupResponse {

    @JsonUnwrapped
    protected final GroupDTO group;
    protected final List<MembershipRestrictedDTO> groupMembers;

    protected GetGroupResponse(GroupDTO group, List<MembershipRestrictedDTO> groupMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
    }

}
