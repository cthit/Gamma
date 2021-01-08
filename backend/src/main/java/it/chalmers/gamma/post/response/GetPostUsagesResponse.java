package it.chalmers.gamma.post.response;

import com.fasterxml.jackson.annotation.JsonValue;
import it.chalmers.gamma.group.response.GetFKITGroupResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostUsagesResponse {
    @JsonValue
    private final List<GetFKITGroupResponse> fkitGroupResponses;

    public GetPostUsagesResponse(List<GetFKITGroupResponse> fkitGroupResponses) {
        this.fkitGroupResponses = fkitGroupResponses;
    }

    public List<GetFKITGroupResponse> getFkitGroupResponses() {
        return this.fkitGroupResponses;
    }

    public GetPostUsagesResponseObject toResponseObject() {
        return new GetPostUsagesResponseObject(this);
    }

    public static class GetPostUsagesResponseObject extends ResponseEntity<GetPostUsagesResponse> {
        GetPostUsagesResponseObject(GetPostUsagesResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
