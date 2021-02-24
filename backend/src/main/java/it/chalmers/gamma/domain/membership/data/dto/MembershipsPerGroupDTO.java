package it.chalmers.gamma.domain.membership.data.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupDTO;

import java.util.List;

public class MembershipsPerGroupDTO {

    @JsonUnwrapped
    private final GroupDTO group;
    private final List<MembershipRestrictedDTO> groupMembers;

    public MembershipsPerGroupDTO(GroupDTO group,
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
