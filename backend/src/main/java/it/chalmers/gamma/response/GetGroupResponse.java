package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetGroupResponse extends ResponseEntity<FKITGroup>{
    public GetGroupResponse(FKITGroup group) {
        super(group, HttpStatus.ACCEPTED);
    }
}
