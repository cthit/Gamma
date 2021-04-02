package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.List;

public class MockGroup {

    public GroupId id;
    public String name;
    public String prettyName;
    public List<MockMembership> members;
    public SuperGroupId superGroup;
    public boolean active;

}
