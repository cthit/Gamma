package it.chalmers.gamma.response.group;

import it.chalmers.gamma.domain.dto.FKITGroupDTO;
import it.chalmers.gamma.domain.dto.FKITSuperGroupDTO;
import it.chalmers.gamma.domain.dto.MembershipDTO;
import it.chalmers.gamma.views.WebsiteDTO;

import java.util.List;

public class GetFKITGroupResponse {

    private final FKITGroupDTO group;
    private final List<MembershipDTO> groupMembers;
    private final List<FKITSuperGroupDTO> superGroup;
    private final List<WebsiteDTO> websites;

    public GetFKITGroupResponse(FKITGroupDTO group, List<MembershipDTO> groupMembers,
                                List<FKITSuperGroupDTO> superGroup, List<WebsiteDTO> websites) {
        this.group = group;
        this.groupMembers = groupMembers;
        this.superGroup = superGroup;
        this.websites = websites;
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

    public List<WebsiteDTO> getWebsites() {
        return websites;
    }
}
