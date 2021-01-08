package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.GroupType;

import java.util.List;
import java.util.UUID;

public class MockFKITSuperGroup {

    private UUID id;
    private String name;
    private String prettyName;
    private GroupType type;
    private List<UUID> groups;

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
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

    public List<UUID> getGroups() {
        return this.groups;
    }

    public void setGroups(List<UUID> groups) {
        this.groups = groups;
    }
}
