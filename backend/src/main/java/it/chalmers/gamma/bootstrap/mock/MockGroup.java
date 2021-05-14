package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import java.util.List;

public record MockGroup(GroupId id,
                        String name,
                        String prettyName,
                        List<MockMembership> members,
                        SuperGroupId superGroupId) { }
