package it.chalmers.delta.response;

import it.chalmers.delta.db.entity.Membership;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetMembershipsResponse extends ResponseEntity<List<Membership>> {
    public GetMembershipsResponse(List<Membership> memberships) {
        super(memberships, HttpStatus.ACCEPTED);
    }
}
