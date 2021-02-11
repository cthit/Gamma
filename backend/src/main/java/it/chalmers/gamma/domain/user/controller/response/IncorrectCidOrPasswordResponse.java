package it.chalmers.gamma.domain.user.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class IncorrectCidOrPasswordResponse extends CustomResponseStatusException {
    public IncorrectCidOrPasswordResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "INCORRECT_CID_OR_PASSWORD");
    }

}
