package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.group.GroupId;
import it.chalmers.gamma.domain.supergroup.SuperGroupId;
import it.chalmers.gamma.domain.text.data.db.Text;
import it.chalmers.gamma.domain.text.data.dto.TextDTO;

import java.util.List;
import java.util.Objects;

public class MockGroup {

    private GroupId id;
    private String name;
    private String prettyName;
    private List<MockMembership> members;
    private SuperGroupId superGroup;

    private boolean active;

    public GroupId getId() {
        return this.id;
    }

    public void setId(GroupId id) {
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

    public List<MockMembership> getMembers() {
        return this.members;
    }

    public void setMembers(List<MockMembership> members) {
        this.members = members;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public SuperGroupId getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(SuperGroupId superGroup) {
        this.superGroup = superGroup;
    }
}
