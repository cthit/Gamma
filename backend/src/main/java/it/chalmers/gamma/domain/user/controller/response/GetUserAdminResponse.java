package it.chalmers.gamma.domain.user.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.membership.data.UserWithGroupsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserAdminResponse {

    @JsonUnwrapped
    private final UserWithGroupsDTO user;

    public GetUserAdminResponse(UserWithGroupsDTO user) {
        this.user = user;
    }

    public GetUserAdminResponseObject toResponseObject() {
        return new GetUserAdminResponseObject(this);
    }

    public static class GetUserAdminResponseObject extends ResponseEntity<GetUserAdminResponse> {
        GetUserAdminResponseObject(GetUserAdminResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
