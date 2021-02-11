package it.chalmers.gamma.domain.post.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.post.data.PostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostResponse {
    @JsonUnwrapped
    private final PostDTO post;

    public GetPostResponse(PostDTO post) {
        this.post = post;
    }

    public PostDTO getPost() {
        return this.post;
    }

    public GetPostResponseObject toResponseObject() {
        return new GetPostResponseObject(this);
    }

    public static class GetPostResponseObject extends ResponseEntity<GetPostResponse> {
        GetPostResponseObject(GetPostResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
