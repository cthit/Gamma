package it.chalmers.gamma.domain.userapproval.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.user.data.dto.UserRestrictedDTO;

import java.util.List;

public class GetUserApprovalsResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public GetUserApprovalsResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

}
