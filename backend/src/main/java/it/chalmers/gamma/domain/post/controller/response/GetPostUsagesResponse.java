package it.chalmers.gamma.domain.post.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

import it.chalmers.gamma.util.domain.GroupWithMembers;

public class GetPostUsagesResponse {

    @JsonValue
    public final List<GroupWithMembers> groups;

    public GetPostUsagesResponse(List<GroupWithMembers> groups) {
        this.groups = groups;
    }

}
