package it.chalmers.gamma.response;

import it.chalmers.gamma.db.entity.Membership;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetMembershipsResponse extends ResponseEntity<List<Membership>> {
    public GetMembershipsResponse(List<Membership> memberships) {
        super(memberships, HttpStatus.ACCEPTED);
    }
}
