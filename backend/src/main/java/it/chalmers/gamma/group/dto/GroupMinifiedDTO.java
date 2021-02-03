package it.chalmers.gamma.group.dto;

import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

public class GroupMinifiedDTO {

    private final String name;
    private final Text function;
    private final String email;
    private final Text description;
    private final UUID id;
    private final String prettyName;

    public GroupMinifiedDTO(GroupDTO g) {
        this.name = g.getName();
        this.function = g.getFunction();
        this.email = g.getEmail();
        this.description = g.getDescription();
        this.id = g.getId();
        this.prettyName = g.getPrettyName();
    }

    public String getName() {
        return this.name;
    }

    public Text getFunction() {
        return this.function;
    }

    public String getEmail() {
        return this.email;
    }

    public Text getDescription() {
        return this.description;
    }

    public UUID getId() {
        return this.id;
    }

    public String getPrettyName() {
        return this.prettyName;
    }
}
