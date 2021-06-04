package it.chalmers.gamma.domain;

import java.util.List;

public record GroupWithMembers(Group group, List<UserPost> members) { }
