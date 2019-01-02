package it.chalmers.gamma.response;

import it.chalmers.gamma.views.FKITGroupView;
import it.chalmers.gamma.views.WebsiteView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class OrderedGroupsResponse extends ResponseEntity<List<FKITGroupView>> {

    public OrderedGroupsResponse(List<FKITGroupView> groups) {
        super(groups, HttpStatus.ACCEPTED);
    }
}
