package it.chalmers.gamma.response.user;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllITUsersMinifiedResponse {
    private final List<GetITUserMinifiedResponse> responses;

    public GetAllITUsersMinifiedResponse(List<GetITUserMinifiedResponse> responses) {
        this.responses = responses;
    }

    public List<GetITUserMinifiedResponse> getResponses() {
        return responses;
    }

    public GetAllITUsersMinifiedResponseObject toResponseObject() {
        return new GetAllITUsersMinifiedResponseObject(this);
    }

    public static class GetAllITUsersMinifiedResponseObject extends ResponseEntity<GetAllITUsersMinifiedResponse> {
        GetAllITUsersMinifiedResponseObject(GetAllITUsersMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
