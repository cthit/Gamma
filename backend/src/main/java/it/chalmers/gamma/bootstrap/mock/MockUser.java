package it.chalmers.gamma.bootstrap.mock;

import it.chalmers.gamma.domain.user.UserId;
import it.chalmers.gamma.util.domain.Cid;

import java.time.Year;
import java.util.UUID;

public class MockUser {

    public UserId id;
    public Cid cid;
    public String nick;
    public String firstName;
    public String lastName;
    public Year acceptanceYear;

}
