package it.chalmers.gamma.response.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;

import it.chalmers.gamma.domain.dto.website.WebsiteUrlDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFKITGroupResponse {
    @JsonUnwrapped
    private final FKITGroupDTO group;
    private final List<MembershipDTO> groupMembers;
    private final List<WebsiteUrlDTO> websites;

    public GetFKITGroupResponse(FKITGroupDTO group, List<MembershipDTO> groupMembers, List<WebsiteUrlDTO> websites) {
        this.group = group;
        this.groupMembers = groupMembers;
        this.websites = websites;
    }

    public GetFKITGroupResponse(FKITGroupDTO group, List<MembershipDTO> groupMembers) {
        this(group, groupMembers, null);
    }

    public FKITGroupDTO getGroup() {
        return this.group;
    }

    public List<MembershipDTO> getGroupMembers() {
        return this.groupMembers;
    }

    public List<WebsiteUrlDTO> getWebsites() {
        return this.websites;
    }

    public GetFKITGroupResponseObject toResponseObject() {
        return new GetFKITGroupResponseObject(this);
    }

    public static class GetFKITGroupResponseObject extends ResponseEntity<GetFKITGroupResponse> {
        public GetFKITGroupResponseObject(GetFKITGroupResponse body) {
            super(body, HttpStatus.OK);
        }
    }
}
