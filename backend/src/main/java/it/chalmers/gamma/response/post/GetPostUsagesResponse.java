package it.chalmers.gamma.response.post;

import it.chalmers.gamma.response.group.GetFKITGroupResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostUsagesResponse {
    private final List<GetFKITGroupResponse> fkitGroupResponses;

    public GetPostUsagesResponse(List<GetFKITGroupResponse> fkitGroupResponses) {
        this.fkitGroupResponses = fkitGroupResponses;
    }

    public List<GetFKITGroupResponse> getFkitGroupResponses() {
        return fkitGroupResponses;
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
