package it.chalmers.gamma.domain.group.data;

import it.chalmers.gamma.domain.DTO;
import it.chalmers.gamma.domain.Email;
import it.chalmers.gamma.domain.group.GroupId;

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
