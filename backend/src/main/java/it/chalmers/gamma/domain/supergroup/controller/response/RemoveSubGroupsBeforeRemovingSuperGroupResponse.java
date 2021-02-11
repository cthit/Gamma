package it.chalmers.gamma.domain.supergroup.controller.response;

import it.chalmers.gamma.response.CustomResponseStatusException;
import org.springframework.http.HttpStatus;

public class RemoveSubGroupsBeforeRemovingSuperGroupResponse extends CustomResponseStatusException {
    public RemoveSubGroupsBeforeRemovingSuperGroupResponse() {
        super(HttpStatus.EXPECTATION_FAILED, "REMOVE_SUB_GROUPS_BEFORE_REMOVING_SUPER_GROUP");
    }
}
