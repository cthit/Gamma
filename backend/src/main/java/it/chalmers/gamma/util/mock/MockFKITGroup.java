package it.chalmers.gamma.util.mock;

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

    /**
     * If true, then this group will be active from today until a year forward
     * If false, then this group was active a year ago to yesterday
     */
    private boolean active;

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

    public Text getDescription() {
        return Objects.requireNonNullElseGet(description, Text::new);
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public Text getFunction() {
        return Objects.requireNonNullElseGet(function, Text::new);
    }

    public void setFunction(Text function) {
        this.function = function;
    }

    public List<MockMembership> getMembers() {
        return members;
    }

    public void setMembers(List<MockMembership> members) {
        this.members = members;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
