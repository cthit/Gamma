package it.chalmers.gamma.domain.post.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class PostIsInUseResponse extends CustomResponseStatusException {
    public PostIsInUseResponse() {
        super(HttpStatus.NOT_ACCEPTABLE, "POST_IS_IN_USE");
    }
}
