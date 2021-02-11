package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class SuperGroupAlreadyExistsResponse extends CustomResponseStatusException {
    public SuperGroupAlreadyExistsResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "SUPER_GROUP_ALREADY_EXISTS");
    }
}
