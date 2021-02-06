package it.chalmers.gamma.approval.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.user.dto.UserRestrictedDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetUserApprovalsResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public List<UserRestrictedDTO> getUsers() {
        return this.users;
    }

    public GetUserApprovalsResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

    public GetUserApprovalsResponseObject toResponseObject() {
        return new GetUserApprovalsResponseObject(this);
    }

    public static class GetUserApprovalsResponseObject extends ResponseEntity<GetUserApprovalsResponse> {
        GetUserApprovalsResponseObject(GetUserApprovalsResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
