package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.membership.data.dto.MembershipsPerGroupDTO;

public class GetActiveGroupResponse {

    @JsonValue
    public final List<MembershipsPerGroupDTO> groups;

    public GetActiveGroupResponse(List<MembershipsPerGroupDTO> groups) {
        this.groups = groups;
    }

}
