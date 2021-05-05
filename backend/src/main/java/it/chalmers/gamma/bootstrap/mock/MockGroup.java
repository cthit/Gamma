package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.internal.group.service.GroupId;
import it.chalmers.gamma.internal.supergroup.service.SuperGroupId;

import java.util.List;

public class MockGroup {

    public GroupId id;
    public String name;
    public String prettyName;
    public List<MockMembership> members;
    public SuperGroupId superGroup;
    public boolean active;

}
