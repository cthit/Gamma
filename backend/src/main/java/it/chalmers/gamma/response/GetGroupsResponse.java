package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITSuperGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetGroupsResponse extends ResponseEntity<List<FKITSuperGroup>> {

    public GetGroupsResponse(List<FKITSuperGroup> groups) {
        super(groups, HttpStatus.ACCEPTED);
    }
}
