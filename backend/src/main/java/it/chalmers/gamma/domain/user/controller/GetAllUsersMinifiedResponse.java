package it.chalmers.gamma.domain.user.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.user.service.UserRestrictedDTO;

public class GetAllUsersMinifiedResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public GetAllUsersMinifiedResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

}
