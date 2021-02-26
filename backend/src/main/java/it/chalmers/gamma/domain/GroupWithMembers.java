package it.chalmers.gamma.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import it.chalmers.gamma.domain.group.data.GroupDTO;

import java.util.List;

public class GroupWithMembers {

    public final GroupDTO group;
    public final List<UserPost> members;

    public GroupWithMembers(GroupDTO group, List<UserPost> members) {
        this.group = group;
        this.members = members;
    }
}
