package it.chalmers.gamma.domain.group.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllGroupResponse {

    @JsonValue
    public final List<GetGroupResponse> groups;

    public GetAllGroupResponse(List<GetGroupResponse> groups) {
        this.groups = groups;
    }

}
