package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.GroupWithMembers;

public class GetAllActiveGroupResponse {

    @JsonValue
    public final List<GroupWithMembers> groups;

    public GetAllActiveGroupResponse(List<GroupWithMembers> groups) {
        this.groups = groups;
    }

}
