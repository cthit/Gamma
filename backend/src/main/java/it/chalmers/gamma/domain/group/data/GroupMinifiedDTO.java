package it.chalmers.gamma.domain.group.data;

import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.text.Text;

import java.util.UUID;

public class GroupMinifiedDTO {

    private final String name;
    private final Email email;
    private final UUID id;
    private final String prettyName;

    public GroupMinifiedDTO(GroupDTO g) {
        this.name = g.getName();
        this.email = g.getEmail();
        this.id = g.getId();
        this.prettyName = g.getPrettyName();
    }

    public String getName() {
        return this.name;
    }

    public Email getEmail() {
        return this.email;
    }

    public UUID getId() {
        return this.id;
    }

    public String getPrettyName() {
        return this.prettyName;
    }
}
