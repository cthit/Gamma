package it.chalmers.gamma.domain.membership.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class MembershipDoesNotExistResponse extends ErrorResponse {
    public MembershipDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
