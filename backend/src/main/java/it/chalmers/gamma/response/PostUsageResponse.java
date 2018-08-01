package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.FKITGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class PostUsageResponse extends ResponseEntity<List<FKITGroup.FKITGroupView>>{

    public PostUsageResponse(List<FKITGroup.FKITGroupView> postUsageTransport) {
        super(postUsageTransport, HttpStatus.ACCEPTED);
    }
}
