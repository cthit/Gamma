package it.chalmers.gamma.app.domain;

import java.util.List;
import java.util.Objects;

public record GroupWithMembers(Group group, List<UserPost> members) {

    public GroupWithMembers {
        Objects.requireNonNull(group);
        Objects.requireNonNull(members);
    }

}
