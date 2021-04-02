package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.data.dto.GroupMinifiedDTO;

public class GetAllGroupMinifiedResponse {

    @JsonValue
    public final List<GroupMinifiedDTO> groups;

    public GetAllGroupMinifiedResponse(List<GroupMinifiedDTO> groups) {
        this.groups = groups;
    }

}
