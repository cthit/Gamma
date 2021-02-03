package it.chalmers.gamma.membership.controller.response;

import it.chalmers.gamma.membership.dto.MembershipRestrictedDTO;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetMembershipResponse {
    public final List<MembershipRestrictedDTO> members;

    public GetMembershipResponse(List<MembershipRestrictedDTO> members) {
        this.members = members;
    }

    public List<MembershipRestrictedDTO> getMembers() {
        return this.members;
    }

    public GetMembershipResponseObject toResponseObject() {
        return new GetMembershipResponseObject(this);
    }

    public static class GetMembershipResponseObject extends ResponseEntity<GetMembershipResponse> {
        public GetMembershipResponseObject(GetMembershipResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
