package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.supergroup.SuperGroupType;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.List;

public class MockSuperGroup {

    public SuperGroupId id;
    public String name;
    public String prettyName;
    public SuperGroupType type;
    public List<GroupId> groups;

}
