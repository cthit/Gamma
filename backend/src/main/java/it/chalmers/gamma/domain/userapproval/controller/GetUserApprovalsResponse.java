package it.chalmers.gamma.domain.userapproval.controller;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;

import java.util.List;

public class GetUserApprovalsResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public GetUserApprovalsResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

}
