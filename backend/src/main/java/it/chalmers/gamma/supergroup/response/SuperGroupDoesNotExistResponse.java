package it.chalmers.gamma.supergroup.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class SuperGroupDoesNotExistResponse extends CustomResponseStatusException {
    public SuperGroupDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "SUPER_GROUP_DOES_NOT_EXIST");
    }
}
