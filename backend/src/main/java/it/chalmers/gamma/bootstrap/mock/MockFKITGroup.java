package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.db.entity.Text;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MockFKITGroup {

    private UUID id;
    private String name;
    private String prettyName;
    private Text description;
    private Text function;
    private List<MockMembership> members;
    private UUID superGroup;

    /**
     * If true, then this group will be active from today until a year forward.
     * If false, then this group was active a year ago to yesterday.
     */
    private boolean active;

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

    public Text getDescription() {
        return Objects.requireNonNullElseGet(this.description, Text::new);
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getFunction() {
        return Objects.requireNonNullElseGet(this.function, Text::new);
    }

    public void setFunction(Text function) {
        this.function = function;
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

    public UUID getSuperGroup() {
        return this.superGroup;
    }

    public void setSuperGroup(UUID superGroup) {
        this.superGroup = superGroup;
    }
}
