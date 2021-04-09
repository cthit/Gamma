package it.chalmers.gamma.domain.supergroup.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.domain.supergroup.service.SuperGroupDTO;

public class GetAllSuperGroupResponse {

    @JsonValue
    private final List<SuperGroupDTO> superGroups;

    protected GetAllSuperGroupResponse(List<SuperGroupDTO> superGroups) {
        this.superGroups = superGroups;
    }

}
