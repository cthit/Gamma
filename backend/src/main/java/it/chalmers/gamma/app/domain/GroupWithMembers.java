package it.chalmers.gamma.app.domain;

import java.util.List;

public record GroupWithMembers(Group group, List<UserPost> members) { }
