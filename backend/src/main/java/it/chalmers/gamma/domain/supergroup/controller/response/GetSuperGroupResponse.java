package it.chalmers.gamma.domain.supergroup.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupDTO;
import it.chalmers.gamma.domain.group.data.GroupMinifiedDTO;
import it.chalmers.gamma.domain.supergroup.data.SuperGroupDTO;

import java.util.List;

public class GetSuperGroupResponse {

    @JsonUnwrapped
    public final SuperGroupDTO superGroup;
    public final List<GroupMinifiedDTO> groups;

    public GetSuperGroupResponse(SuperGroupDTO superGroup, List<GroupMinifiedDTO> groups) {
        this.superGroup = superGroup;
        this.groups = groups;
    }

}

