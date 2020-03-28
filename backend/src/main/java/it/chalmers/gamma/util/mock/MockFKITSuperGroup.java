package it.chalmers.gamma.util.mock;

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
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public List<UUID> getGroups() {
        return groups;
    }

    public void setGroups(List<UUID> groups) {
        this.groups = groups;
    }
}
