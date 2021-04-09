package it.chalmers.gamma.domain.group.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.GroupWithMembers;

public class GetAllActiveGroupResponse {

    @JsonValue
    protected final List<GroupWithMembers> groups;

    protected GetAllActiveGroupResponse(List<GroupWithMembers> groups) {
        this.groups = groups;
    }

}
