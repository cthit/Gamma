package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import it.chalmers.gamma.util.domain.UserWithGroups;

public class GetUserRestrictedResponse {

    @JsonUnwrapped
    public final UserWithGroups user;

    public GetUserRestrictedResponse(UserWithGroups user) {
        this.user = user;
    }
}
