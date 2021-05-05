package it.chalmers.gamma.util.domain;

import it.chalmers.gamma.internal.group.service.GroupDTO;

import java.util.List;

public record GroupWithMembers(GroupDTO group, List<UserPost> members) { }
