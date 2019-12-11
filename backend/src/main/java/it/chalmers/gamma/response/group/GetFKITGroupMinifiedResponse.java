package it.chalmers.gamma.response.group;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITMinifiedGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetFKITGroupMinifiedResponse {
    @JsonUnwrapped
    private final FKITMinifiedGroupDTO group;

    public GetFKITGroupMinifiedResponse(FKITMinifiedGroupDTO group) {
        this.group = group;
    }

    public FKITMinifiedGroupDTO getGroup() {
        return group;
    }

    public GetFKITGroupMinifiedResponseObject toResponseObject() {
        return new GetFKITGroupMinifiedResponseObject(this);
    }

    public static class GetFKITGroupMinifiedResponseObject extends ResponseEntity<GetFKITGroupMinifiedResponse> {
        GetFKITGroupMinifiedResponseObject(GetFKITGroupMinifiedResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
