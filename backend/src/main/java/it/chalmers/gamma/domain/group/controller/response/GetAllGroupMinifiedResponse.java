package it.chalmers.gamma.domain.group.controller.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.domain.group.data.GroupMinifiedDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetAllGroupMinifiedResponse {

    @JsonValue
    public final List<GroupMinifiedDTO> groups;

    public GetAllGroupMinifiedResponse(List<GroupMinifiedDTO> groups) {
        this.groups = groups;
    }

}
