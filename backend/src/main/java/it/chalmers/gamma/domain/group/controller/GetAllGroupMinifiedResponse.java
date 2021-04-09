package it.chalmers.gamma.domain.group.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.service.GroupMinifiedDTO;

public class GetAllGroupMinifiedResponse {

    @JsonValue
    protected final List<GroupMinifiedDTO> groups;

    protected GetAllGroupMinifiedResponse(List<GroupMinifiedDTO> groups) {
        this.groups = groups;
    }

}
