package it.chalmers.gamma.domain.membership.controller.response;

import it.chalmers.gamma.util.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class MembershipNotFoundResponse extends ErrorResponse {

    public MembershipNotFoundResponse() {
        super(HttpStatus.NOT_FOUND);
    }

}
