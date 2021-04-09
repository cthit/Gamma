package it.chalmers.gamma.domain.supergroup.controller;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.service.GroupMinifiedDTO;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;

import java.util.List;

public class GetSuperGroupResponse {

    @JsonUnwrapped
    private final SuperGroupDTO superGroup;
    private final List<GroupMinifiedDTO> groups;

    protected GetSuperGroupResponse(SuperGroupDTO superGroup, List<GroupMinifiedDTO> groups) {
        this.superGroup = superGroup;
        this.groups = groups;
    }

}

