package it.chalmers.gamma.response.post;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class PostDoesNotExistResponse extends CustomResponseStatusException {
    public PostDoesNotExistResponse() {
        super(HttpStatus.NOT_FOUND, "POST_DOES_NOT_EXIST");
    }
}
