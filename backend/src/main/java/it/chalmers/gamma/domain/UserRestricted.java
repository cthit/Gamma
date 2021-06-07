package it.chalmers.gamma.domain;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.User;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Year;

public record UserRestricted(UserId id, Cid cid, Nick nick, FirstName firstName, LastName lastName, Year acceptanceYear) implements DTO {

    public UserRestricted(User u) {
        this(u.id(), u.cid(), u.nick(), u.firstName(), u.lastName(), u.acceptanceYear());
    }

}
