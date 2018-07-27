package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GroupsResponse extends ResponseEntity<List<FKITGroup>> {


    public GroupsResponse(List<FKITGroup> groups) {
        super(groups, HttpStatus.OK);
    }
}
