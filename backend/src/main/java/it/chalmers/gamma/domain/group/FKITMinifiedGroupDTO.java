package it.chalmers.gamma.domain.group;

import it.chalmers.gamma.db.entity.Text;

import java.util.UUID;

public class FKITMinifiedGroupDTO {

    private final String name;
    private final Text function;
    private final String email;
    private final Text description;
    private final UUID id;
    private final String prettyName;

    public FKITMinifiedGroupDTO(String name, Text function,
                                String email, Text description, UUID id, String prettyName) {
        this.name = name;
        this.function = function;
        this.email = email;
        this.description = description;
        this.id = id;
        this.prettyName = prettyName;
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
