package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.domain.Cid;
import it.chalmers.gamma.domain.FirstName;
import it.chalmers.gamma.domain.LastName;
import it.chalmers.gamma.domain.Nick;
import it.chalmers.gamma.domain.UserId;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Year;

public record UserRestrictedDTO(UserId id, Cid cid, Nick nick, FirstName firstName, LastName lastName, Year acceptanceYear) implements DTO {

    public UserRestrictedDTO(UserDTO u) {
        this(u.id(), u.cid(), u.nick(), u.firstName(), u.lastName(), u.acceptanceYear());
    }

}
