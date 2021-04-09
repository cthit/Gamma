package it.chalmers.gamma.domain.group.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.service.GroupMinifiedDTO;

public class GetGroupMinifiedResponse {

    @JsonValue
    protected final GroupMinifiedDTO group;

    protected GetGroupMinifiedResponse(GroupMinifiedDTO group) {
        this.group = group;
    }
}
