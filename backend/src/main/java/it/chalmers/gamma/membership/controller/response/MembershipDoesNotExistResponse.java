package it.chalmers.gamma.membership.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class MembershipDoesNotExistResponse extends CustomResponseStatusException {
    public MembershipDoesNotExistResponse() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "MEMBERSHIP_DOES_NOT_EXIST");
    }
}
