package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.util.domain.UserWithGroups;

import java.util.List;

public class GetAllUsersResponse {

    @JsonValue
    private final List<UserWithGroups> users;

    public GetAllUsersResponse(List<UserWithGroups> users) {
        this.users = users;
    }

}
