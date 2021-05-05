package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.user.service.UserId;
import it.chalmers.gamma.util.domain.Cid;

public record MockUser(
        UserId id,
        Cid cid,
        String nick,
        String firstName,
        String lastName,
        int acceptanceYear) { }
