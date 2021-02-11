package it.chalmers.gamma.domain.membership.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MembershipNotFoundResponse extends CustomResponseStatusException {

    public MembershipNotFoundResponse() {
        super(HttpStatus.NOT_FOUND, "MEMBERSHIP_NOT_FOUND");
    }

}
