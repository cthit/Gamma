package it.chalmers.gamma.domain.post.controller;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

import it.chalmers.gamma.util.domain.GroupWithMembers;

public class GetPostUsagesResponse {

    @JsonValue
    private final List<GroupWithMembers> groups;

    protected GetPostUsagesResponse(List<GroupWithMembers> groups) {
        this.groups = groups;
    }

}
