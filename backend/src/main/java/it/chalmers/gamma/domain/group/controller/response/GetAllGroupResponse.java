package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.GroupWithMembers;

public class GetAllGroupResponse {

    @JsonValue
    public final List<GroupWithMembers> groups;

    public GetAllGroupResponse(List<GroupWithMembers> groups) {
        this.groups = groups;
    }

}
