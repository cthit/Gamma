package it.chalmers.gamma.response.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.chalmers.gamma.db.entity.WebsiteURL;
import it.chalmers.gamma.domain.dto.group.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.group.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.membership.MembershipDTO;
import it.chalmers.gamma.domain.dto.website.WebsiteDTO;

import it.chalmers.gamma.domain.dto.website.WebsiteURLDTO;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetFKITGroupResponse {

    private final FKITGroupDTO group;
    private final List<MembershipDTO> groupMembers;
    private final List<FKITSuperGroupDTO> superGroup;
    private final List<WebsiteURLDTO> websites;

    public GetFKITGroupResponse(FKITGroupDTO group, List<MembershipDTO> groupMembers,
                                List<FKITSuperGroupDTO> superGroup, List<WebsiteURLDTO> websites) {
        this.group = group;
        this.groupMembers = groupMembers;
        this.superGroup = superGroup;
        this.websites = websites;
    }

    public GetFKITGroupResponse(FKITGroupDTO group, List<MembershipDTO> groupMembers) {
        this(group, groupMembers, null, null);
    }

    public FKITGroupDTO getGroup() {
        return group;
    }

    public List<MembershipDTO> getGroupMembers() {
        return groupMembers;
    }

    public List<FKITSuperGroupDTO> getSuperGroup() {
        return superGroup;
    }

    public List<WebsiteURLDTO> getWebsites() {
        return websites;
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
