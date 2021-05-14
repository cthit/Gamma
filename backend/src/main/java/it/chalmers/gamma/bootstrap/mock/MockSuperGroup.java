package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;
import it.chalmers.gamma.internal.supergrouptype.service.SuperGroupTypeName;

import java.util.List;

public record MockSuperGroup(SuperGroupId id,
                             String name,
                             String prettyName,
                             SuperGroupTypeName type,
                             List<GroupId> groups) {
}
