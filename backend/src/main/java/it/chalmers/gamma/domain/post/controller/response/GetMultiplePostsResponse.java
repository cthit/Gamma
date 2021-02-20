package it.chalmers.gamma.domain.post.controller.response;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.domain.post.data.PostDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetMultiplePostsResponse {

    @JsonValue
    private final List<PostDTO> posts;

    public GetMultiplePostsResponse(List<PostDTO> posts) {
        this.posts = posts;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public GetMultiplePostsResponseObject toResponseObject() {
        return new GetMultiplePostsResponseObject(this);
    }

    public static class GetMultiplePostsResponseObject extends ResponseEntity<GetMultiplePostsResponse> {
        GetMultiplePostsResponseObject(GetMultiplePostsResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}

