package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.response.ErrorResponse;
import org.springframework.http.HttpStatus;

public class RemoveSubGroupsBeforeRemovingSuperGroupResponse extends ErrorResponse {
    public RemoveSubGroupsBeforeRemovingSuperGroupResponse() {
        super(HttpStatus.EXPECTATION_FAILED);
    }
}
