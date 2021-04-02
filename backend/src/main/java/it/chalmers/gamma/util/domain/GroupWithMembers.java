package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.domain.group.data.dto.GroupDTO;

import java.util.List;

public class GroupWithMembers {

    public final GroupDTO group;
    public final List<UserPost> members;

    public GroupWithMembers(GroupDTO group, List<UserPost> members) {
        this.group = group;
        this.members = members;
    }
}
