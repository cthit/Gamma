package it.chalmers.gamma.internal.user.service;

import it.chalmers.gamma.util.domain.Cid;
import it.chalmers.gamma.util.domain.abstraction.DTO;

import java.time.Year;

public record UserRestrictedDTO(UserId id, Cid cid, String nick, String firstName, String lastName, Year acceptanceYear) implements DTO {

    public UserRestrictedDTO(UserDTO u) {
        this(u.id(), u.cid(), u.nick(), u.firstName(), u.lastName(), u.acceptanceYear());
    }

}
