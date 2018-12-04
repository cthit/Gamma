package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITGroup;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GroupsResponse extends ResponseEntity<List<FKITGroup>> {


    public GroupsResponse(List<FKITGroup> groups) {
        super(groups, HttpStatus.OK);
    }
}
