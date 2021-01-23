package it.chalmers.gamma.approval.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.user.UserRestrictedDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUserApprovalResponse {

    @JsonValue
    private final List<UserRestrictedDTO> users;

    public List<UserRestrictedDTO> getUsers() {
        return this.users;
    }

    public GetAllITUserApprovalResponse(List<UserRestrictedDTO> users) {
        this.users = users;
    }

    public GetAllITUserApprovalResponseObject toResponseObject() {
        return new GetAllITUserApprovalResponseObject(this);
    }

    public static class GetAllITUserApprovalResponseObject extends ResponseEntity<GetAllITUserApprovalResponse> {
        GetAllITUserApprovalResponseObject(GetAllITUserApprovalResponse body) {
            super(body, HttpStatus.OK);
        }
    }

}
