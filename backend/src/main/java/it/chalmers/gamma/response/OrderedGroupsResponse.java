package it.chalmers.gamma.response;

import it.chalmers.gamma.views.FKITGroupView;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class OrderedGroupsResponse extends ResponseEntity<List<FKITGroupView>> {

    public OrderedGroupsResponse(List<FKITGroupView> groups) {
        super(groups, HttpStatus.ACCEPTED);
    }
}
