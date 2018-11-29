package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetPostResponse extends ResponseEntity<Post> {
    public GetPostResponse(Post post) {
        super(post, HttpStatus.ACCEPTED);
    }
}
