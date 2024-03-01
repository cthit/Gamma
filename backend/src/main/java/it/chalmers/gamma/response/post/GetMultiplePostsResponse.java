package it.chalmers.gamma.response.post;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetMultiplePostsResponse {
    @JsonValue
    private final List<GetPostResponse> getPostResponse;

    public GetMultiplePostsResponse(List<GetPostResponse> getPostResponses) {
        this.getPostResponse = getPostResponses;
    }

    public List<GetPostResponse> getGetPostResponse() {
        return this.getPostResponse;
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

