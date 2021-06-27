package it.chalmers.gamma.app.domain;

import it.chalmers.gamma.adapter.secondary.jpa.util.DTO;

public record UserRestricted(UserId id, Cid cid, Nick nick, FirstName firstName, LastName lastName, AcceptanceYear acceptanceYear) implements DTO {

    public UserRestricted(User u) {
        this(u.id(), u.cid(), u.nick(), u.firstName(), u.lastName(), u.acceptanceYear());
    }

}
