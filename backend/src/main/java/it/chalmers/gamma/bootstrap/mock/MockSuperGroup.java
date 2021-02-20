package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.GroupType;
import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;

import java.util.List;
import java.util.UUID;

public class MockSuperGroup {

    private SuperGroupId id;
    private String name;
    private String prettyName;
    private GroupType type;
    private List<GroupId> groups;

    public SuperGroupId getId() {
        return this.id;
    }

    public void setId(SuperGroupId id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public GroupType getType() {
        return this.type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public List<GroupId> getGroups() {
        return this.groups;
    }

    public void setGroups(List<GroupId> groups) {
        this.groups = groups;
    }
}
