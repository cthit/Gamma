package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetPostResponse extends ResponseEntity<List<Post>>{
    public GetPostResponse(List<Post> post) {
        super(post, HttpStatus.ACCEPTED);
    }
}

