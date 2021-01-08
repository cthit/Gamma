package it.chalmers.gamma.approval.response;

import com.fasterxml.jackson.annotation.JsonValue;

import it.chalmers.gamma.domain.user.ITUserRestrictedDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUserApprovalResponse {

    @JsonValue
    private final List<ITUserRestrictedDTO> users;

    public List<ITUserRestrictedDTO> getUsers() {
        return this.users;
    }

    public GetAllITUserApprovalResponse(List<ITUserRestrictedDTO> users) {
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
