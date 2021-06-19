package it.chalmers.gamma.domain;

import it.chalmers.gamma.util.domain.abstraction.DTO;

public record UserRestricted(UserId id, Cid cid, Nick nick, FirstName firstName, LastName lastName, AcceptanceYear acceptanceYear) implements DTO {

    public UserRestricted(User u) {
        this(u.id(), u.cid(), u.nick(), u.firstName(), u.lastName(), u.acceptanceYear());
    }

}
