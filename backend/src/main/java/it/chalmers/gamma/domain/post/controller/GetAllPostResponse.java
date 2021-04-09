package it.chalmers.gamma.domain.post.controller;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

import it.chalmers.gamma.domain.post.service.PostDTO;

public class GetAllPostResponse {

    @JsonValue
    protected final List<PostDTO> posts;

    protected GetAllPostResponse(List<PostDTO> posts) {
        this.posts = posts;
    }

}

