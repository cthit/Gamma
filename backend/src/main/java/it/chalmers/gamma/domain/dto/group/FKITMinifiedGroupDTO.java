package it.chalmers.gamma.domain.dto.group;

import it.chalmers.gamma.db.entity.Text;

import java.util.UUID;

public class FKITMinifiedGroupDTO {

    private final String name;
    private final Text function;
    private final String email;
    private final Text description;
    private final UUID id;

    public FKITMinifiedGroupDTO(String name, Text function,
                                String email, Text description, UUID id) {
        this.name = name;
        this.function = function;
        this.email = email;
        this.description = description;
        this.id = id;
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
}
