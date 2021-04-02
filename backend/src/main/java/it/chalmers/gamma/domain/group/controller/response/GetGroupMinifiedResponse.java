package it.chalmers.gamma.domain.group.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.data.dto.GroupMinifiedDTO;

public class GetGroupMinifiedResponse {

    @JsonValue
    public final GroupMinifiedDTO group;

    public GetGroupMinifiedResponse(GroupMinifiedDTO group) {
        this.group = group;
    }
}
