package it.chalmers.gamma.domain.supergroup.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupWithMembersDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class GetActiveGroupsWithMembersResponse {

    @JsonUnwrapped
    private final List<GroupWithMembersDTO> groups;

    public GetActiveGroupsWithMembersResponse(List<GroupWithMembersDTO> groups) {
        this.groups = groups;
    }

    public List<GroupWithMembersDTO> getGroups() {
        return groups;
    }

    public static class GetActiveGroupsWithMembersResponseObject extends ResponseEntity<GetActiveGroupsWithMembersResponse> {

        public GetActiveGroupsWithMembersResponseObject(GetActiveGroupsWithMembersResponse body) {
            super(body, HttpStatus.OK);
        }

    }

}
