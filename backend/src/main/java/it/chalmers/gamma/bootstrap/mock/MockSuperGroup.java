package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.supergroup.service.SuperGroupType;
import it.chalmers.gamma.domain.group.service.GroupId;
import it.chalmers.gamma.domain.supergroup.service.SuperGroupId;

import java.util.List;

public class MockSuperGroup {

    public SuperGroupId id;
    public String name;
    public String prettyName;
    public SuperGroupType type;
    public List<GroupId> groups;

}
