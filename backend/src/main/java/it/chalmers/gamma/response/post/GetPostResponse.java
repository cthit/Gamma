package it.chalmers.gamma.response.post;

import it.chalmers.gamma.domain.dto.post.PostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostResponse {
    private final PostDTO post;

    public GetPostResponse(PostDTO post) {
        this.post = post;
    }

    public PostDTO getPost() {
        return post;
    }

    public GetPostResponseObject toResponseObject() {
        return new GetPostResponseObject(this);
    }

    public static class GetPostResponseObject extends ResponseEntity<GetPostResponse>{
        GetPostResponseObject(GetPostResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
