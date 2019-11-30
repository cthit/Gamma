package it.chalmers.gamma.response.group;

import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GetMembershipResponse{
    public final List<MembershipDTO> members;

    public GetMembershipResponse(List<MembershipDTO> members) {
        this.members = members;
    }

    public List<MembershipDTO> getMembers() {
        return members;
    }

    public GetMembershipResponseObject getResponseObject() {
        return new GetMembershipResponseObject(this);
    }

    public static class GetMembershipResponseObject extends ResponseEntity<GetMembershipResponse> {
        public GetMembershipResponseObject(GetMembershipResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
