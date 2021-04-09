package it.chalmers.gamma.domain.group.service;

import it.chalmers.gamma.util.domain.abstraction.DTO;
import it.chalmers.gamma.util.domain.Email;

public class GroupMinifiedDTO implements DTO {

    private final String name;
    private final Email email;
    private final GroupId id;
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

    public GroupId getId() {
        return this.id;
    }

    public String getPrettyName() {
        return this.prettyName;
    }
}
