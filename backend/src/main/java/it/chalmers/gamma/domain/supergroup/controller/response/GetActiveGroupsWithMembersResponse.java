package it.chalmers.gamma.domain.supergroup.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.membership.data.dto.MembershipsPerGroupDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetActiveGroupsWithMembersResponse {

    @JsonUnwrapped
    private final List<MembershipsPerGroupDTO> groups;

    public GetActiveGroupsWithMembersResponse(List<MembershipsPerGroupDTO> groups) {
        this.groups = groups;
    }

    public List<MembershipsPerGroupDTO> getGroups() {
        return groups;
    }

    public static class GetActiveGroupsWithMembersResponseObject extends ResponseEntity<GetActiveGroupsWithMembersResponse> {

        public GetActiveGroupsWithMembersResponseObject(GetActiveGroupsWithMembersResponse body) {
            super(body, HttpStatus.OK);
        }

    }

}
