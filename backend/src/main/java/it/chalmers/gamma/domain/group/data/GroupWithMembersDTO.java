package it.chalmers.gamma.domain.group.data;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.membership.data.MembershipRestrictedDTO;

import java.util.List;

public class GroupWithMembersDTO {

    @JsonUnwrapped
    private final GroupDTO group;
    private final List<MembershipRestrictedDTO> groupMembers;

    public GroupWithMembersDTO(GroupDTO group,
                               List<MembershipRestrictedDTO> groupMembers) {
        this.group = group;
        this.groupMembers = groupMembers;
    }

    public GroupDTO getGroup() {
        return group;
    }

    public List<MembershipRestrictedDTO> getGroupMembers() {
        return groupMembers;
    }

}
